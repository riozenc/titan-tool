/**
 *	
 * @Title:PersistanceManager.java
 * @author Riozen
 *	@date 2013-11-13 下午4:06:04
 *	
 */
package com.riozenc.titanTool.mybatis.persistence;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.riozenc.titanTool.mybatis.pagination.Page;

public class PersistanceManager {

	private String dbName;

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public PersistanceManager() {

	}

	protected SqlSession getSession() {
		return null;
	}

	/**
	 * 根据主键查询
	 * 
	 * @param namespace
	 * @param obj
	 * @return
	 */
	public <T> T load(String namespace, Object obj) {
		return getSession().selectOne(namespace, obj);
	}

	/**
	 * 根据条件查询
	 * 
	 * @param namespace
	 * @param obj
	 * @return
	 */
	public <T> List<T> find(String namespace, Object obj) {
		if (Page.class.isAssignableFrom(obj.getClass())) {
			((Page) obj).setDbName(getDbName());
		}
		return getSession().selectList(namespace, obj);
	}

	/**
	 * 新增
	 * 
	 * @param namespace
	 * @param obj
	 * @return
	 */
	public int insert(String namespace, Object obj) {
		return getSession().insert(namespace, obj);
	}

	/**
	 * 修改
	 * 
	 * @param namespace
	 * @param obj
	 * @return
	 */
	public int update(String namespace, Object obj) {
		return getSession().update(namespace, obj);
	}

	/**
	 * 删除
	 * 
	 * @param namespace
	 * @param obj
	 * @return
	 */
	public int delete(String namespace, Object obj) {
		return getSession().delete(namespace, obj);
	}

	/**
	 * 批量插入 20w数据入库37秒 太通用不灵便，需要单独实现2015/6/14暂未想好
	 * 
	 * @param namespace
	 * @param list
	 * @return
	 */
	public int insertList(String namespace, List<?> list) {
		int i = 0;
		for (Object obj : list) {
			// i += insert(namespace, obj);
			i++;
			insert(namespace, obj);
		}
		return i;
	}

	/**
	 * 批量更新
	 * 
	 * @param namespace
	 * @param list
	 * @return
	 */
	public int updateList(String namespace, List<?> list) {
		int i = 0;
		for (Object obj : list) {
			i += update(namespace, obj);
		}
		return i;
	}

	/**
	 * 批量删除
	 * 
	 * @param namespace
	 * @param list
	 * @return
	 */
	public int deleteList(String namespace, List<?> list) {
		int i = 0;
		for (Object obj : list) {
			i += delete(namespace, obj);
		}
		return i;
	}

}
