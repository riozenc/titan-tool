/**
 *    Auth:riozenc
 *    Date:2019年3月11日 下午5:32:24
 *    Title:com.riozenc.titanTool.datasources.DatasourcesConfig.java
 **/
package com.riozenc.titanTool.datasources;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;

import com.riozenc.titanTool.datasources.pool.DataSourcePoolFactory;
import com.riozenc.titanTool.datasources.type.DruidDatabaseType;
import com.riozenc.titanTool.properties.Global;

public class DatasourcesConfig {

	@Bean
	public void dataSourcePool() throws SQLException, Exception {
		String dbValue = Global.getConfig("db");
		String[] dbs = dbValue.split(",");
		for (String db : dbs) {
			DataSourcePoolFactory.createFactory(db, createDatabase(db));
		}

	}

	private DataSource createDatabase(String dbName) throws SQLException {
		DruidDatabaseType databaseType = new DruidDatabaseType(dbName);
		return databaseType.build();
	}

}
