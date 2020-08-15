/**
 * Author : czy
 * Date : 2019年4月9日 上午11:05:22
 * Title : com.riozenc.titanTool.mongo.MongoPoolFactory.java
 *
**/
package com.riozenc.titanTool.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.riozenc.titanTool.properties.Global;

public class MongoPoolFactory {

	private static MongoClient mongoClient;

	public static void init() {
		if (mongoClient == null) {
			ConnectionString connectionString = new ConnectionString(Global.getConfig("mongo.connectionString"));
			MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
					.applyConnectionString(connectionString).applyToConnectionPoolSettings(settings -> {
						settings.maxSize(Integer.valueOf(Global.getConfig("mongo.maxSize", "100")));
					}).retryWrites(true).build();
			mongoClient = MongoClients.create(mongoClientSettings);
		}
	}

	public static MongoClient getMongoClient() {
		return mongoClient;
	}
}
