/**
 * @Title:DAOProxyFactory.java
 * @author:Riozenc
 * @datetime:2015年6月2日 上午10:52:41
 */
package com.riozenc.titanTool.spring.webapp.dao.proxy;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.riozenc.titanTool.common.string.ObjectToStringUtil;
import com.riozenc.titanTool.mybatis.persistence.PersistanceManager;

public class DAOProxyFactory implements MethodInterceptor {

	private static final Log logger = LogFactory.getLog(DAOProxyFactory.class);

	// private Object targetObject;
	private PersistanceManager persistanceManager;

	private DAOProxyFactory() {
	}

	public static DAOProxyFactory getInstance() {
		return new DAOProxyFactory();
	}

	public Object createProxy(PersistanceManager persistanceManager) {
		this.persistanceManager = persistanceManager;
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(PersistanceManager.class);
		enhancer.setCallback(this);

		return enhancer.create();
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		// TODO Auto-generated method stub
		String methodName = method.getName();
		try {
			Object rev = method.invoke(persistanceManager, args);
			persistanceManager.getSession().commit(true);
			return rev;
		} catch (Exception e) {
			// 回滚
			persistanceManager.getSession().rollback(true);
			logger.error("失败..." + args[0] + "的" + methodName + "操作被回滚...\r\n" + e.getMessage());
			for (Object bad : persistanceManager.getBadList()) {
				logger.error("错误数据:" + ObjectToStringUtil.execute(bad));
			}
			e.printStackTrace();
			logger.error(e);
			return null;
		} finally {
			// 最终处理
			persistanceManager.getSession().close();
		}
	}

}
