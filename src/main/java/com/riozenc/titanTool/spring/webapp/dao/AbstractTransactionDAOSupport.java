/**
 * Title:AbstractDAOSupport.java
 * Author:czy
 * Datetime:2016年10月28日 下午3:12:01
 */
package com.riozenc.titanTool.spring.webapp.dao;

import com.riozenc.titanTool.mybatis.persistence.PersistanceManager;
import com.riozenc.titanTool.mybatis.persistence.PersistanceManager2;

public abstract class AbstractTransactionDAOSupport extends AbstractDAOSupport {

	protected PersistanceManager getPersistanceManager() {
		return getPersistanceManager(false);
	}

	protected PersistanceManager getPersistanceManager(boolean isProxy) {
		return getPersistanceManager(getDbName(), getExecutorType(), isProxy);
	}

	protected PersistanceManager2 getPersistanceManager2(boolean isProxy) {
		return getPersistanceManager2(getDbName(), getExecutorType(), isProxy);
	}

}
