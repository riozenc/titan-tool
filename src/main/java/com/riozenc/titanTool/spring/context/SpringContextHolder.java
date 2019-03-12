/**
 * @Project:quicktool
 * @Title:SpringContextholder.java
 * @Author:Riozenc
 * @Datetime:2016年11月1日 下午9:22:36
 * 
 */
package com.riozenc.titanTool.spring.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
	private static final Log logger = LogFactory.getLog(SpringContextHolder.class);
	private static ApplicationContext applicationContext = null;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		if (SpringContextHolder.applicationContext == null) {
			SpringContextHolder.applicationContext = applicationContext;
			logger.info("完成applicationContext赋值,SpringContextHolder功能启用.");
		}
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		applicationContext = null;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}

	private static void assertContextInjected() {
		if (applicationContext == null) {
			throw new IllegalStateException("applicaitonContext属性未注入, 请在applicationContext.xml中定义SpringContextHolder.");
		}
	}

}
