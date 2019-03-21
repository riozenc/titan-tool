/**
 * Title:TransactionServiceFactoryBean.java
 * Author:czy
 * Datetime:2016年11月9日 上午10:07:47
 */
package com.riozenc.titanTool.spring.transaction.bean;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import com.riozenc.titanTool.annotation.TransactionDAO;
import com.riozenc.titanTool.annotation.utils.AnnotationUtil;
import com.riozenc.titanTool.common.reflect.ReflectUtil;
import com.riozenc.titanTool.common.string.StringUtils;
import com.riozenc.titanTool.spring.context.SpringContextHolder;
import com.riozenc.titanTool.spring.transaction.proxy.TransactionServiceProxyFactory2;
import com.riozenc.titanTool.spring.webapp.dao.AbstractTransactionDAOSupport;

public class TransactionServiceFactoryBean<T> implements FactoryBean<T> {

	@Autowired
	private AbstractAutowireCapableBeanFactory beanFacotry;
	@Autowired
	private ApplicationContext applicationContext;

	private static Map<String, BeanDefinitionHolder> definitionHolderMap = new ConcurrentHashMap<>();
	private Class<T> serviceInterface;

	public TransactionServiceFactoryBean() {
		// intentionally empty
	}

	public TransactionServiceFactoryBean(Class<T> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public void setServiceInterface(Class<T> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	@Override
	public T getObject() throws Exception {

		// TODO Auto-generated method stub
		if (this.serviceInterface == null) {
			afterPropertiesSet();
		}
		return TransactionServiceProxyFactory2.getInstance().createProxy(build());
	}

	@Override
	public Class<?> getObjectType() {
		// TODO Auto-generated method stub
		return this.serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

	public static Map<String, BeanDefinitionHolder> getDefinitionHolderMap() {
		return definitionHolderMap;
	}

	public void afterPropertiesSet() throws Exception {
		throw new Exception("没有可实例化的目标...");
	}

	private T build() throws Exception {
		T service = beanFacotry.createBean(serviceInterface);
		Field[] fields = this.serviceInterface.getDeclaredFields();
		for (Field field : fields) {
			if (null != field.getAnnotation(TransactionDAO.class)) {
				ReflectUtil.setFieldValue(service, field.getName(), processTransactionDAO(field));// 给service赋值dao
			}
		}
		return service;
	}

	private AbstractTransactionDAOSupport processTransactionDAO(Field dao) throws Exception {
		if (null != dao.getAnnotation(TransactionDAO.class)) {

			BeanDefinitionHolder beanDefinitionHolder = definitionHolderMap
					.get(StringUtils.decapitalize(dao.getType().getSimpleName()));
			if (beanDefinitionHolder == null) {
				throw new Exception(dao.getType() + " is not found @TransactionDAO!");
			}

//			AbstractTransactionDAOSupport abstractDAOSupport = (AbstractTransactionDAOSupport) BeanUtils
//					.instantiateClass(dao.getType());
			AbstractTransactionDAOSupport abstractDAOSupport = (AbstractTransactionDAOSupport) applicationContext
					.getBean(dao.getType());

			String dbName = (String) AnnotationUtil.getAnnotationValue(dao, TransactionDAO.class);
			if (dbName.length() < 1) {
				dbName = (String) AnnotationUtil.getAnnotationValue(dao.getType(), TransactionDAO.class);
			}
			ReflectUtil.setFieldValue(abstractDAOSupport, "dbName", dbName);

			return abstractDAOSupport;
		}
		return null;
	}

}
