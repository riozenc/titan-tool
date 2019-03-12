/**
 *    Auth:riozenc
 *    Date:2019年3月11日 下午4:37:26
 *    Title:com.riozenc.titanTool.datasources.type.DruidDatabaseType.java
 **/
package com.riozenc.titanTool.datasources.type;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.riozenc.titanTool.properties.Global;

public class DruidDatabaseType extends BaseDatabase {

	public String type;
	private boolean autoCommit;
	private String filters;
	private String validationQuery;
	private boolean testWhileIdle;
	private boolean testOnBorrow;
	private boolean testOnReturn;
	private boolean poolPreparedStatements;
	private int initialSize;
	private int minIdle;
	private int maxActive;
	private int maxWait;
	private int timeBetweenEvictionRunsMillis;
	private int minEvictableIdleTimeMillis;

	public DruidDatabaseType(String dbName) {
		// TODO Auto-generated constructor stub
		setDrive(Global.getConfig(join(dbName, "driverClassName")));
		setUrl(Global.getConfig(join(dbName, "url")));
		setUsername(Global.getConfig(join(dbName, "username")));
		setPassword(Global.getConfig(join(dbName, "password")));
		this.type = Global.getConfig(join(dbName, "jdbc.type"));
		this.autoCommit = Boolean.parseBoolean(Global.getConfig(join(dbName, "autoCommit")));
		this.filters = Global.getConfig(join(dbName, "filters"));
		this.validationQuery = Global.getConfig(join(dbName, "validationQuery"));
		this.testWhileIdle = Boolean.parseBoolean(Global.getConfig(join(dbName, "testWhileIdle")));
		this.testOnBorrow = Boolean.parseBoolean(Global.getConfig(join(dbName, "testOnBorrow")));
		this.testOnReturn = Boolean.parseBoolean(Global.getConfig(join(dbName, "testOnReturn")));
		this.poolPreparedStatements = Boolean.parseBoolean(Global.getConfig(join(dbName, "poolPreparedStatements")));
		this.initialSize = Integer.parseInt(Global.getConfig(join(dbName, "initialSize")));
		this.minIdle = Integer.parseInt(Global.getConfig(join(dbName, "minIdle")));
		this.maxActive = Integer.parseInt(Global.getConfig(join(dbName, "maxActive")));
		this.maxWait = Integer.parseInt(Global.getConfig(join(dbName, "maxWait")));
		this.timeBetweenEvictionRunsMillis = Integer
				.parseInt(Global.getConfig(join(dbName, "timeBetweenEvictionRunsMillis")));
		this.minEvictableIdleTimeMillis = Integer
				.parseInt(Global.getConfig(join(dbName, "minEvictableIdleTimeMillis")));

	}

	public DataSource build() throws SQLException {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setDriverClassName(getDrive());
		druidDataSource.setUrl(getUrl());
		druidDataSource.setUsername(getUsername());
		druidDataSource.setPassword(getPassword());
		druidDataSource.addFilters(this.filters);
		druidDataSource.setValidationQuery(this.validationQuery);
		druidDataSource.setTestWhileIdle(this.testWhileIdle);
		druidDataSource.setTestOnBorrow(this.testOnBorrow);
		druidDataSource.setTestOnReturn(this.testOnReturn);
		druidDataSource.setPoolPreparedStatements(this.poolPreparedStatements);
		druidDataSource.setInitialSize(this.initialSize);
		druidDataSource.setMinIdle(this.minIdle);
		druidDataSource.setMaxActive(this.maxActive);
		druidDataSource.setMaxWait(this.maxWait);
		druidDataSource.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);
		druidDataSource.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
		druidDataSource.setDefaultAutoCommit(this.autoCommit);
		return druidDataSource;
	}
}
