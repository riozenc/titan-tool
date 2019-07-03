/**
 * Author : czy
 * Date : 2019年4月18日 下午7:37:21
 * Title : com.riozenc.titanTool.mongo.dao.MongoDAO.java
 *
**/
package com.riozenc.titanTool.mongo.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.riozenc.titanTool.common.json.utils.JSONUtil;
import com.riozenc.titanTool.common.string.StringUtils;
import com.riozenc.titanTool.mongo.spring.MongoTemplateFactory;
import com.riozenc.titanTool.mybatis.pagination.interceptor.PaginationInterceptor;
import com.riozenc.titanTool.properties.Global;

public interface MongoDAOSupport {
	final Log logger = LogFactory.getLog(PaginationInterceptor.class);
	static final String separatorChar = "#";

	default MongoTemplate getMongoTemplate() {
		if (StringUtils.isEmpty(Global.getConfig("mongo.databaseName"))) {
			logger.info("mongo.databaseName is null");
			return null;
		}
		return getMongoTemplate(Global.getConfig("mongo.databaseName"));
	}

	default MongoTemplate getMongoTemplate(String datebaseName) {
		MongoTemplate mongoTemplate = MongoTemplateFactory.getInstance().getMongoTemplate(datebaseName);
		return mongoTemplate;
	}

	default String getCollectionName(String date, String name) {
		return date + separatorChar + name;
	}

	default List<Document> toDocuments(List<?> list) {
		List<Document> documents = Collections.synchronizedList(new ArrayList<>());
		list.parallelStream().forEach(m -> {
			documents.add(Document.parse(JSONUtil.toJsonString(m)));
		});
		return documents;
	}

	default <T> List<Document> toDocuments(List<T> list, ToDocumentCallBack<T> callBack) {
		List<Document> documents = Collections.synchronizedList(new ArrayList<>());
		list.parallelStream().forEach(m -> {
			documents.add(Document.parse(JSONUtil.toJsonString(callBack.call(m))));
		});
		return documents;
	}

	default MongoCollection<Document> getCollection(String date, String name) {
		return getMongoTemplate().getCollection(getCollectionName(date, name));
	}

	default void insertMany(MongoCollection<Document> collection, List<Document> documents) {
		collection.insertMany(documents);
	}

	default List<WriteModel<Document>> insertMany(List<Document> documents) {
		List<WriteModel<Document>> requests = new ArrayList<>();
		documents.stream().forEach(d -> {
			InsertOneModel<Document> insertOneModel = new InsertOneModel<Document>(d);
			requests.add(insertOneModel);
		});
		return requests;
	}

	default List<WriteModel<Document>> updateMany(List<Document> documents, MongoUpdateFilter mongoUpdateFilter,
			boolean isUpsert) {
		List<WriteModel<Document>> requests = new ArrayList<>();
		documents.stream().forEach(d -> {
			Bson filter = mongoUpdateFilter.filter(d);
			Bson update = mongoUpdateFilter.update(d);
			UpdateOneModel<Document> updateOneModel = new UpdateOneModel<>(filter, update,
					new UpdateOptions().upsert(isUpsert));

			requests.add(updateOneModel);
		});
		return requests;
	}

	default List<String> findMany(MongoCollection<?> collection, MongoFindFilter filter) {
		FindIterable<String> findIterable = collection.find(filter.filter(), String.class);
		MongoCursor<String> mongoCursor = findIterable.iterator();
		List<String> result = new ArrayList<>();
		while (mongoCursor.hasNext()) {
			result.add(mongoCursor.next());
		}
		return result;
	}

	default <T> List<T> findMany(MongoCollection<Document> collection, MongoFindFilter filter, Class<T> clazz) {
		FindIterable<Document> findIterable = collection.find(filter.filter());
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		List<T> result = new ArrayList<>();
		while (mongoCursor.hasNext()) {
			Document document = mongoCursor.next();
			try {
				result.add(JSONUtil.readValue(
						document.toJson(JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()), clazz));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	interface MongoFindFilter {
		Bson filter();
	}

	interface MongoUpdateFilter {
		Bson filter(Document param);

		default Bson update(Document param) {
			return new Document("$set", param);
		}
	}

	interface MongoDeleteFilter {
		Document filter();
	}

	interface ToDocumentCallBack<T> {
		T call(T t);
	}
}
