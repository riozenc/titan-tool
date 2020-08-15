/**
 * Author : czy
 * Date : 2019年4月9日 上午11:36:35
 * Title : com.riozenc.titanTool.mongo.spring.MongoTemplateFactory.java
 *
**/
package com.riozenc.titanTool.mongo.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.riozenc.titanTool.mongo.MongoPoolFactory;

public class MongoTemplateFactory {

	private static final MongoTemplateFactory MONGO_TEMPLATE_FACTORY = new MongoTemplateFactory();
	private static final Map<String, MongoTemplate> MAP = new ConcurrentHashMap<String, MongoTemplate>();;

	public static MongoTemplateFactory getInstance() {
		return MONGO_TEMPLATE_FACTORY;
	}

	public MongoTemplate getMongoTemplate(String databaseName) {

		if (databaseName == null || databaseName.length() == 0) {
			throw new IllegalArgumentException("getMongoTemplate dbName不能为空!!!");
		}

		if (MAP.get(databaseName) == null) {
			MongoTemplate mongoTemplate = new MongoTemplate(MongoPoolFactory.getMongoClient(), databaseName);
			MAP.put(databaseName, mongoTemplate);
		}
		return MAP.get(databaseName);

	}
}
