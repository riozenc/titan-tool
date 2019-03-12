/**
 * Title:AbstractDAOSupport.java
 * Author:czy
 * Datetime:2016年10月28日 下午3:12:01
 */
package com.riozenc.titanTool.spring.webapp.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;

import com.riozenc.titanTool.mybatis.persistence.PersistanceManager;
import com.riozenc.titanTool.mybatis.session.SqlSessionManager;
import com.riozenc.titanTool.spring.webapp.dao.proxy.DAOProxyFactory;

public abstract class AbstractDAOSupport {
	private static final Log logger = LogFactory.getLog(AbstractDAOSupport.class);
	private ExecutorType executorType = ExecutorType.SIMPLE;
	private boolean isProxy = false;
	private boolean autoCommit = false;
	private String dbName = null;
	private String NAMESPACE = null;

	private ThreadLocal<Map<String, SqlSession>> localSqlSessionMap = new ThreadLocal<>();

	public AbstractDAOSupport() {

	}

	protected PersistanceManager getPersistanceManager() {
		return getPersistanceManager(this.executorType, this.autoCommit, this.isProxy);
	}

	protected PersistanceManager getPersistanceManager(boolean autoCommit) {

		return getPersistanceManager(this.executorType, autoCommit, this.isProxy);
	}

	protected PersistanceManager getPersistanceManager(ExecutorType executorType) {
		return getPersistanceManager(executorType, this.autoCommit, this.isProxy);
	}

	protected PersistanceManager getPersistanceManager(ExecutorType executorType, boolean autoCommit, boolean isProxy) {

		return getPersistanceManager(this.dbName, executorType, autoCommit, isProxy);
	}

	protected PersistanceManager getPersistanceManager(String dbName, ExecutorType executorType, boolean autoCommit,
			boolean isProxy) {

		Long l = System.currentTimeMillis();

		SqlSession sqlSession = getSqlSessionMap().get(dbName + executorType);

		if (sqlSession == null) {
			sqlSession = SqlSessionManager.getSession(dbName, executorType);
		} else {
			try {
				if (sqlSession.getConnection().isClosed()) {
					getSqlSessionMap().remove(sqlSession);
					sqlSession = SqlSessionManager.getSession(dbName, executorType);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				getSqlSessionMap().remove(sqlSession);
				sqlSession = SqlSessionManager.getSession(dbName, executorType);
			}
		}

		logger.info("[" + Thread.currentThread().getName() + "]获取SqlSession(" + sqlSession.getConnection() + ")用时:"
				+ (System.currentTimeMillis() - l) / 1000);
		getSqlSessionMap().put(dbName + executorType, sqlSession);

		if (isProxy) {
			return (PersistanceManager) DAOProxyFactory.getInstance()
					.createProxy(new PersistanceManager(dbName,sqlSession, autoCommit));
		} else {
			return new PersistanceManager(dbName, sqlSession, autoCommit);
		}
	}

	public String getNamespace() {
		if (null == NAMESPACE) {
			NAMESPACE = this.getClass().getName();
		}
		return NAMESPACE;
	}

	public Collection<SqlSession> getSqlSessions() {
		return getSqlSessionMap().values();
	}

	protected String getDbName() {
		return this.dbName;
	}

	protected ExecutorType getExecutorType() {
		return executorType;
	}

	private Map<String, SqlSession> getSqlSessionMap() {
		if (localSqlSessionMap.get() == null) {
			localSqlSessionMap.set(new HashMap<String, SqlSession>());
		}
		return localSqlSessionMap.get();
	}
}
