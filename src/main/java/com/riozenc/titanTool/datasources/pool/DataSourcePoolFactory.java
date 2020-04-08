/**
 *    Auth:riozenc
 *    Date:2018年6月5日 下午5:41:04
 *    Title:com.riozenc.titanTool.datasources.pool.DruidDataSourcePoolFactory.java
 **/
package com.riozenc.titanTool.datasources.pool;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.riozenc.titanTool.mybatis.MybatisEntity;
import com.riozenc.titanTool.properties.Global;

public class DataSourcePoolFactory {

	private static final Log logger = LogFactory.getLog(DataSourcePoolFactory.class);

	private static boolean FLAG = false;
	private static String defaultDB = null;
	private static SqlSessionFactory sqlSessionFactory;

	private static Map<String, SqlSessionFactory> DBS = new LinkedHashMap<String, SqlSessionFactory>();

	public void initDataSourcePoolFactory() {

	}

	public static SqlSessionFactory createFactory(String name, DataSource dataSource) throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource);
		factoryBean.setTypeAliasesPackage(Global.getConfig("namespace"));
		factoryBean.setTypeAliasesSuperType(MybatisEntity.class);
		factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
				.getResources("classpath:/" + Global.getConfig("namespace").replaceAll("\\.", "/") + "/**/webapp/**/*.xml"));
		factoryBean.setConfigLocation(
				new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));

		putDB(name, factoryBean.getObject());

		return factoryBean.getObject();
	}

	private static void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		if (DataSourcePoolFactory.sqlSessionFactory == null) {
			DataSourcePoolFactory.sqlSessionFactory = sqlSessionFactory;
		}
	}

	/**
	 * 获取默认数据库连接工厂,多数据源情况下取第一个
	 * 
	 * @return
	 */
	public static SqlSessionFactory getSqlSessionFactory() {
		if (FLAG) {
			return sqlSessionFactory;
		} else {
			throw new RuntimeException("数据库未完成初始化...");
		}
	}

	/**
	 * 获取默认数据库连接工厂,根据name
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static SqlSessionFactory getSqlSessionFactory(String name) throws Exception {
		if (FLAG) {
			if (null == name || "".equals(name)) {
				name = defaultDB;
			}
			return DBS.get(name);
		} else {
			throw new Exception("数据库未完成初始化...");
		}
	}

	private static void putDB(String key, SqlSessionFactory sessionFactory) {
		if (defaultDB == null) {
			defaultDB = key;
		}
		DBS.put(key, sessionFactory);
		FLAG = checkSqlSessionFactory(DBS.get(key));
		if (FLAG) {
			logger.info(key + " 数据库创建成功...");
		}
	}

	/**
	 * 校验数据库是否可用
	 * 
	 * @param sqlSession
	 * @return
	 */
	private static boolean checkSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			if (sqlSession.getConnection().isValid(2000)) {
				setSqlSessionFactory(sqlSessionFactory);
				return true;
			} else {
				logger.info("数据库连接校验等待超时...请检查...");
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			return false;
		} finally {
			sqlSession.close();
		}
	}

}
