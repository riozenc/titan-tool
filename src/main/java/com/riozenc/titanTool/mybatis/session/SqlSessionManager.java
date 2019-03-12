/**
 * Title:SqlSessionManager.java
 * author:Riozen
 * datetime:2015年3月17日 下午8:20:21
 */

package com.riozenc.titanTool.mybatis.session;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.riozenc.titanTool.datasources.pool.DataSourcePoolFactory;


public class SqlSessionManager {
	private static SqlSessionFactory sqlSessionFactory = null;

	private static byte[] b = new byte[0];

	private SqlSessionManager() {
	}

	public static SqlSession getSession() {

		return getSession(ExecutorType.SIMPLE);
	}

	public static SqlSession getSession(String dbName, boolean autoCommit) {
		synchronized (b) {
			try {
				return DataSourcePoolFactory.getSqlSessionFactory(dbName).openSession(autoCommit);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 不自动提交
		return sqlSessionFactory.openSession(autoCommit);
	}

	public static SqlSession getSession(ExecutorType executorType) {
		if (null == sqlSessionFactory) {
			synchronized (b) {
				sqlSessionFactory = DataSourcePoolFactory.getSqlSessionFactory();
			}
		}
		// 不自动提交
		return sqlSessionFactory.openSession(executorType, false);
	}

	public static SqlSession getSession(ExecutorType executorType, boolean autoCommit) {
		if (null == sqlSessionFactory) {
			synchronized (b) {
				sqlSessionFactory = DataSourcePoolFactory.getSqlSessionFactory();
			}
		}
		// 不自动提交
		return sqlSessionFactory.openSession(executorType, autoCommit);
	}

	public static SqlSession getSession(String dbName, ExecutorType executorType) {

		synchronized (b) {
			try {
				return DataSourcePoolFactory.getSqlSessionFactory(dbName).openSession(executorType, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 不自动提交
		return sqlSessionFactory.openSession(executorType, false);
	}
}
