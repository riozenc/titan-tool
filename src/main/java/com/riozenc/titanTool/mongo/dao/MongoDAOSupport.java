/**
 * Author : czy
 * Date : 2019年4月18日 下午7:37:21
 * Title : com.riozenc.titanTool.mongo.dao.MongoDAO.java
 *
**/
package com.riozenc.titanTool.mongo.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.riozenc.titanTool.common.json.utils.GsonUtils;
import com.riozenc.titanTool.common.json.utils.JSONUtil;
import com.riozenc.titanTool.common.string.StringUtils;
import com.riozenc.titanTool.mongo.spring.MongoTemplateFactory;
import com.riozenc.titanTool.properties.Global;

public interface MongoDAOSupport {
	final Log logger = LogFactory.getLog(MongoDAOSupport.class);
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
//			documents.add(Document.parse(JSONUtil.toJsonString(m)));
			documents.add(Document.parse(GsonUtils.toJsonIgnoreNull(m)));
		});
		return documents;
	}

	default <T> List<Document> toDocuments(List<T> list, ToDocumentCallBack<T> callBack) {
		List<Document> documents = Collections.synchronizedList(new ArrayList<>());
		list.parallelStream().forEach(m -> {
//			documents.add(Document.parse(JSONUtil.toJsonString(callBack.call(m))));
			documents.add(Document.parse(GsonUtils.toJsonIgnoreNull(callBack.call(m))));
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

	// TODO 后期扩展：多库，多次查询后汇总结果
	default <T> List<T> findMany(String collectionName, MongoFindFilter filter, Class<T> clazz) {
		return findMany(getMongoTemplate().getCollection(collectionName), filter, clazz);
	}

	default <T> List<T> findManySort(String collectionName, MongoFindFilter filter, Class<T> clazz) {
		MongoCollection<Document> collection = getMongoTemplate().getCollection(collectionName);
		FindIterable<Document> findIterable = collection.find(filter.filter()).sort(filter.getSort());
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		List<T> result = new ArrayList<>();
		while (mongoCursor.hasNext()) {
			Document document = mongoCursor.next();
			result.add(GsonUtils.readValue(
					document.toJson(JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()), clazz));
		}
		logger.info(
				collection.getNamespace().getFullName() + "::" + filter.filter().toString() + "====" + result.size());
		return result;
	}

	default <T> List<T> findMany(MongoCollection<Document> collection, MongoFindFilter filter, Class<T> clazz) {
		FindIterable<Document> findIterable = collection.find(filter.filter());
		MongoCursor<Document> mongoCursor = findIterable.iterator();
		List<T> result = new ArrayList<>();
		while (mongoCursor.hasNext()) {
			Document document = mongoCursor.next();
			result.add(GsonUtils.readValue(
					document.toJson(JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()), clazz));
		}

		logger.info(
				collection.getNamespace().getFullName() + "::" + filter.filter().toString() + "====" + result.size());
		return result;
	}

	default List<Document> aggregate(String collectionName, MongoAggregateFilter filter) {
		MongoCollection<Document> collection = getMongoTemplate().getCollection(collectionName);

		List<? extends Bson> pipeline = filter.getPipeline();

		AggregateIterable<Document> aggregateIterable = collection.aggregate(pipeline);

		MongoCursor<Document> mongoCursor = aggregateIterable.iterator();

		List<Document> result = new ArrayList<>();
		while (mongoCursor.hasNext()) {
			Document document = mongoCursor.next();
			result.add(document);
		}

		logger.info(collection.getNamespace().getFullName() + "::" + filter.getMatch().toString() + "+"
				+ filter.getGroup() + "====" + result.size());

		return result;
	}

	interface MongoFindFilter {
		Bson filter();

		default Bson getSort() {
			return new Document("id", 1);
		}

		/**
		 * 升序
		 * 
		 * @return
		 */
		default int up() {
			return 1;
		}

		/**
		 * 降序
		 * 
		 * @return
		 */
		default int down() {
			return -1;
		}
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

	interface MongoAggregateFilter {
		default List<? extends Bson> getPipeline() {
			List<Bson> pipeLine = new LinkedList<>();
			pipeLine.add(getMatch());
			pipeLine.add(getGroup());
			return pipeLine;
		};

		Bson setGroup();

		Bson setMatch();

		default Bson getGroup() {
			return new Document("$group", setGroup());
		};

		default Bson getMatch() {
			return new Document("$match", setMatch());
		}

	}

	interface ToDocumentCallBack<T> {
		T call(T t);
	}
}
