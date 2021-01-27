/**
 *	
 * @Title:PersistanceManager.java
 * @author Riozen
 *	@date 2013-11-13 下午4:06:04
 *	
 */
package com.riozenc.titanTool.mybatis.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import com.riozenc.titanTool.mybatis.MybatisEntity;
import com.riozenc.titanTool.mybatis.pagination.Page;

import reactor.core.publisher.Flux;

public class PersistanceManager2 {

	private String dbName;
	private SqlSession session;
	private int batchLimit = 1000;

	private List<MybatisEntity> badList = new ArrayList<>();

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public SqlSession getSession() {
		return session;
	}

	public void setSession(SqlSession session) {
		this.session = session;
	}

	public PersistanceManager2(SqlSession session) {
		this.session = session;
	}

	public PersistanceManager2(String dbName, SqlSession session) {
		this.dbName = dbName;
		this.session = session;
	}

	public PersistanceManager2() {

	}

	public List<?> getBadList() {
		return badList;
	}

	/**
	 * 根据主键查询
	 * 
	 * @param namespace
	 * @param obj
	 * @return
	 */
	public <T> T load(String namespace, Object obj) {
		return session.selectOne(namespace, obj);
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
		return session.selectList(namespace, obj);
	}

	/**
	 * 新增
	 * 
	 * @param namespace
	 * @param obj
	 * @return
	 */
	public int insert(String namespace, Object obj) {
		return session.insert(namespace, obj);
	}

	/**
	 * 修改
	 * 
	 * @param namespace
	 * @param obj
	 * @return
	 */
	public int update(String namespace, Object obj) {
		return session.update(namespace, obj);
	}

	/**
	 * 删除
	 * 
	 * @param namespace
	 * @param obj
	 * @return
	 */
	public int delete(String namespace, Object obj) {
		return session.delete(namespace, obj);
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

	/**
	 * 强制批量插入
	 * 
	 * @param namespace
	 * @param list
	 * @param forceExcute强制执行
	 * @return 错误数据个数
	 */
	public int insertList(String namespace, List<MybatisEntity> list, boolean forceExcute) {
		if (forceExcute) {
			return batchExcute(namespace, list, batchLimit, new Handler() {
				@Override
				public int handle(Object obj) {
					// TODO Auto-generated method stub
					return insert(namespace, obj);
				}
			});
		} else {
			return insertList(namespace, list);
		}
	}

	/**
	 * 强制批量更新
	 * 
	 * @param namespace
	 * @param list
	 * @param forceExcute
	 * @return
	 */
	public int updateList(String namespace, List<MybatisEntity> list, boolean forceExcute) {
		if (forceExcute) {
			return batchExcute(namespace, list, batchLimit, new Handler() {
				@Override
				public int handle(Object obj) {
					// TODO Auto-generated method stub

					return update(namespace, obj);
				}
			});
		} else {
			return updateList(namespace, list);
		}
	}

	/**
	 * 强制批量删除
	 * 
	 * @param namespace
	 * @param list
	 * @param forceExcute
	 * @return
	 */
	public int deleteList(String namespace, List<MybatisEntity> list, boolean forceExcute) {
		if (forceExcute) {
			return batchExcute(namespace, list, batchLimit, new Handler() {
				@Override
				public int handle(Object obj) {
					// TODO Auto-generated method stub
					return delete(namespace, obj);
				}
			});
		} else {
			return deleteList(namespace, list);
		}
	}

	/**
	 * 流式查询测试
	 * 
	 * @param <T>
	 * @param namespace
	 * @param obj
	 * @return
	 */
	public <T> Flux<T> scan(String namespace, T obj) {
		if (Page.class.isAssignableFrom(obj.getClass())) {
			((Page) obj).setDbName(getDbName());
		}

		Cursor<T> cursor = session.selectCursor(namespace, obj);

		return Flux.fromStream(StreamSupport.stream(cursor.spliterator(), false));
	}

	public <T> Flux<T> scanOnRowBounds(String namespace, T obj, RowBounds rowBounds) {
		if (Page.class.isAssignableFrom(obj.getClass())) {
			((Page) obj).setDbName(getDbName());
		}

		Cursor<T> cursor = session.selectCursor(namespace, obj, rowBounds);

		return Flux.fromStream(StreamSupport.stream(cursor.spliterator(), false));
	}

	/**
	 * 批量处理
	 * 
	 * @param namespace
	 * @param list
	 * @param limit
	 * @param handler
	 * @return
	 */
	private int batchExcute(String namespace, List<MybatisEntity> list, int limit, Handler handler) {
		if (limit == 1) {
			excuteOne(namespace, list, handler);
		} else {
			excuteList(namespace, list, limit, handler);
		}
		return this.badList.size();
	}

	/**
	 * 批量提交方法单条处理
	 * 
	 * @param namespace
	 * @param list
	 * @param handler
	 */
	private void excuteOne(String namespace, List<MybatisEntity> list, Handler handler) {
		list.stream().forEach(r -> {
			int i = handler.handle(r);
			if (!commit() || 0 == i) {
				this.badList.add(r);
			}
		});

	}

	/**
	 * 批量提交方法多条处理
	 * 
	 * @param namespace
	 * @param list
	 * @param limit
	 * @param handler
	 */
	private void excuteList(String namespace, List<MybatisEntity> list, int limit, Handler handler) {
		List<MybatisEntity> tempList = new ArrayList<>();
		List<MybatisEntity> badList = new ArrayList<>();
		for (MybatisEntity obj : list) {
			tempList.add(obj);
			handler.handle(obj);
			if (tempList.size() == limit) {
				if (!commit()) {
					badList.addAll(tempList);
				}
				tempList.clear();
			}
		}
		if (list.size() % limit != 0) {
			if (!commit()) {
				badList.addAll(tempList);
			}
			tempList.clear();
		}
		if (badList.size() > 0 && limit != 1) {

			batchExcute(namespace, badList, getCycle(badList.size(), 1000), handler);
		}
		this.badList = badList;
	}

	private boolean commit() {
		try {
			this.session.commit(true);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			this.session.rollback(true);
			return false;
		}
	}

	/**
	 * 根据size大小获取循环次数
	 * 
	 * @param size
	 * @param limit
	 * @return
	 */
	private int getCycle(int size, int limit) {
		if (size < 10) {
			return 1;
		}
		if (size % limit == size) {
			return getCycle(size, limit / 10);
		} else if ((size + limit - 1) % limit == 0) {
			return getCycle(size, limit * 10);
		} else {
			return limit;
		}
	}

	private interface Handler {
		public int handle(Object obj);
	}
}
