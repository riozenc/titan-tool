/**
 * Title:AbstractDAOSupport.java
 * Author:czy
 * Datetime:2016年10月28日 下午3:12:01
 */
package com.riozenc.titanTool.spring.webapp.dao;

import com.riozenc.titanTool.mybatis.persistence.PersistanceManager;

public abstract class AbstractTransactionDAOSupport extends AbstractDAOSupport {

	protected PersistanceManager getPersistanceManager() {
		return getPersistanceManager(false);
	}

	protected PersistanceManager getPersistanceManager(boolean autoCommit) {
		return getPersistanceManager(autoCommit, false);
	}

	protected PersistanceManager getPersistanceManager(boolean autoCommit, boolean isProxy) {
		return getPersistanceManager(getDbName(), getExecutorType(), autoCommit, isProxy);
	}

}
