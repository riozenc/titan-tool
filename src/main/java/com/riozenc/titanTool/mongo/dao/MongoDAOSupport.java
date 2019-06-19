/**
 * Author : czy
 * Date : 2019年4月18日 下午7:37:21
 * Title : com.riozenc.titanTool.mongo.dao.MongoDAO.java
 *
**/
package com.riozenc.titanTool.mongo.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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

	default void insertMany(MongoCollection<Document> collection, List<Document> documents) {
		collection.insertMany(documents);
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

	default <T> List<T> findMany(MongoCollection<?> collection, MongoFindFilter filter, Class<T> clazz) {
		FindIterable<T> findIterable = collection.find(filter.filter(), clazz);
		MongoCursor<T> mongoCursor = findIterable.iterator();

		List<T> result = new ArrayList<>();
		while (mongoCursor.hasNext()) {
			result.add(mongoCursor.next());
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

}
