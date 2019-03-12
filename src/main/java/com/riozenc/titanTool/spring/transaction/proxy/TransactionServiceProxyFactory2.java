/**
 * @Title:DAOProxyFactory.java
 * @author:Riozenc
 * @datetime:2015年6月2日 上午10:52:41
 */
package com.riozenc.titanTool.spring.transaction.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.riozenc.titanTool.annotation.TransactionDAO;
import com.riozenc.titanTool.common.date.DateUtil;
import com.riozenc.titanTool.common.reflect.ReflectUtil;
import com.riozenc.titanTool.spring.webapp.dao.AbstractDAOSupport;


public class TransactionServiceProxyFactory2 implements MethodInterceptor {

	private static final Log logger = LogFactory.getLog(TransactionServiceProxyFactory2.class);

	private ThreadLocal<LinkedHashMap<Integer, SqlSession>> threadLocal = new ThreadLocal<>();

	private Object targetObject;
	private Class<?> clazz;

	private TransactionServiceProxyFactory2() {
	}

	public static TransactionServiceProxyFactory2 getInstance() {

		return new TransactionServiceProxyFactory2();
	}

	public LinkedHashMap<Integer, SqlSession> getSqlSessionMap() {
		if (threadLocal.get() == null) {
			threadLocal.set(new LinkedHashMap<Integer, SqlSession>());
		}
		return threadLocal.get();
	}

	@SuppressWarnings("unchecked")
	public <T> T createProxy(Object obj) throws InstantiationException, IllegalAccessException {

		if (null == obj) {
			throw new ClassCastException(DateUtil.getDate() + "代理对象不存在....");
		} else {
			if (obj instanceof Object) {
				this.targetObject = obj;
				this.clazz = obj.getClass();
				Enhancer enhancer = new Enhancer();
				enhancer.setSuperclass(obj.getClass());
				enhancer.setCallback(this);
				return (T) enhancer.create();
			} else {
				throw new ClassCastException(DateUtil.getDate() + "代理对象未构建....");
			}
		}
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		// TODO Auto-generated method stub

		// buildDAO();
		String methodName = method.getName();

		try {
			if (methodName.startsWith("get") || methodName.startsWith("find")) {// 查询方法无事务
				logger.info(methodName + " is select method,no transaction");
				Object rev = method.invoke(targetObject, args);
				recovery();// 回收sqlSession！注意select也需要回收
				return rev;
			}
			method.setAccessible(true);// 垃圾回收时，无法调用protected方法
			Object rev = method.invoke(targetObject, args);
			commit(methodName);
			return rev;
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			logger.error(e);
			return null;
		} finally {
			// 最终处理
			for (Entry<Integer, SqlSession> entry : getSqlSessionMap().entrySet()) {
				close(entry.getValue());
			}
			getSqlSessionMap().clear();
		}
	}

	/**
	 * 回收service中dao的sqlSession
	 */
	private void recovery() {
		// targetObject;
		Field[] fields = this.clazz.getDeclaredFields();

		for (Field field : fields) {
			if (null != field.getAnnotation(TransactionDAO.class)) {
				AbstractDAOSupport abstractDAOSupport = (AbstractDAOSupport) ReflectUtil.getFieldValue(targetObject,
						field.getName());

				for (SqlSession sqlSession : abstractDAOSupport.getSqlSessions()) {
					close(getSqlSessionMap().put(sqlSession.hashCode(), sqlSession));
				}
				abstractDAOSupport.getSqlSessions().clear();
			}
		}
	}

	

	private void close(SqlSession sqlSession) {
		if (sqlSession != null) {
			sqlSession.close();
			sqlSession = null;
		}
	}

	private void commit(String methodName) throws SQLException, Exception {
		recovery();// 回收sqlSession
		for (Entry<Integer, SqlSession> entry : getSqlSessionMap().entrySet()) {
			if (entry.getValue() != null) {
				if (entry.getValue().getConnection().getAutoCommit()) {
					logger.error(methodName + "方法存在事务自动提交,事务管理无效.");
				} else {
					entry.getValue().commit();// connection autocommit=true时 失效
				}
			}
		}
	}

	private void rollback() throws SQLException {
		recovery();// 回收sqlSession
		for (Entry<Integer, SqlSession> entry : getSqlSessionMap().entrySet()) {
			if (entry.getValue() != null) {
				entry.getValue().rollback();// connection autocommit=true时 失效
				// entry.getValue().getConnection().rollback();
			}
		}
	}
}
