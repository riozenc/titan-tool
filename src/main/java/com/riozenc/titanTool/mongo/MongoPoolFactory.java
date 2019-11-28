/**
 * Author : czy
 * Date : 2019年4月9日 上午11:05:22
 * Title : com.riozenc.titanTool.mongo.MongoPoolFactory.java
 *
**/
package com.riozenc.titanTool.mongo;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.riozenc.titanTool.properties.Global;

public class MongoPoolFactory {
	// key: 数据库名称；value:对应的数据库连接

	private static MongoClient mongoClient;

	public static void init(String username, String password, String databaseName) {
		if (mongoClient == null) {
			MongoCredential credential = MongoCredential.createScramSha1Credential(username, databaseName,
//			MongoCredential credential = MongoCredential.createCredential(username, databaseName,//mongo2.0
					password.toCharArray());

			mongoClient = new MongoClient(getSeeds(), credential, createMongoClientOptions());
		}
	}

	public static MongoClient getMongoClient(String databaseName) {

		return mongoClient;
	}

	private static MongoClientOptions createMongoClientOptions() {

		MongoClientOptions.Builder builder = new MongoClientOptions.Builder().cursorFinalizerEnabled(true)
				.connectionsPerHost(Integer.valueOf(Global.getConfig("mongo.connectionsPerHost", "10")))
				.threadsAllowedToBlockForConnectionMultiplier(
						Integer.valueOf(Global.getConfig("mongo.threadsAllowedToBlockForConnectionMultiplier", "5")))
				.connectTimeout(Integer.valueOf(Global.getConfig("mongo.connectTimeout", "10000")))
				.socketTimeout(Integer.valueOf(Global.getConfig("mongo.socketTimeout", "0")))
				.maxWaitTime(Integer.valueOf(Global.getConfig("mongo.maxWaitTime", "0")))
				.writeConcern(WriteConcern.ACKNOWLEDGED);

		// <!--1.primary：主节点，默认模式，读操作只在主节点，如果主节点不可用，报错或者抛出异常。-->
		// <!--2.primaryPreferred：首选主节点，大多情况下读操作在主节点，如果主节点不可用，如故障转移，读操作在从节点。-->
		// <!--3.secondary：从节点，读操作只在从节点，如果从节点不可用，报错或者抛出异常。-->
		// <!--4.secondaryPreferred：首选从节点，大多情况下读操作在从节点，特殊情况（如单主节点架构）读操作在主节点。-->
		// <!--5.nearest：最邻近节点，读操作在最邻近的成员，可能是主节点或者从节点，关于最邻近的成员请参考。-->
		String readReference = Global.getConfig("mongo.options.readReference");
		if ("secondaryPreferred".equals(readReference)) {
			builder.readPreference(ReadPreference.secondaryPreferred());
		} else if ("secondary".equals(readReference)) {
			builder.readPreference(ReadPreference.secondary());
		} else if ("primaryPreferred".equals(readReference)) {
			builder.readPreference(ReadPreference.primaryPreferred());
		} else if ("nearest".equals(readReference)) {
			builder.readPreference(ReadPreference.nearest());
		} else {
			builder.readPreference(ReadPreference.primary());
		}

		return builder.build();

	}

	private static List<ServerAddress> getSeeds() {
		String servers = Global.getConfig("mongo.hostPort");
		if (servers != null && servers.length() > 0) {
			String[] serverArray = servers.split(",");
			String[] host_port = null;
			List<ServerAddress> seeds = new ArrayList<ServerAddress>();
			for (String server : serverArray) {
				host_port = server.split(":");
				try {
					if (host_port.length == 2) {
						seeds.add(new ServerAddress(host_port[0], Integer.parseInt(host_port[1])));
					} else {
						seeds.add(new ServerAddress(host_port[0], 27017));
					}
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Bad mongodb port defined : " + host_port[1]);
				}
			}
			return seeds;
		} else {
			throw new IllegalArgumentException("No mongodb hostPort defined!");
		}
	}
}
