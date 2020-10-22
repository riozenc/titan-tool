/**
 * @Project:quicktool
 * @Title:TransactionManager.java
 * @Author:Riozenc
 * @Datetime:2016年11月7日 下午10:51:24
 * 
 */
package com.riozenc.titanTool.spring.transaction.registry;

import java.lang.annotation.Annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;

import com.riozenc.titanTool.annotation.TransactionDAO;
import com.riozenc.titanTool.common.string.StringUtils;
import com.riozenc.titanTool.properties.Global;
import com.riozenc.titanTool.spring.transaction.scanner.ClassPathTransactionDAOScanner;

public abstract class TransactionDAORegistryPostProcessor
		implements IDefinitionRegistryProcessor, BeanDefinitionRegistryPostProcessor {
	private static final Log logger = LogFactory.getLog(TransactionDAORegistryPostProcessor.class);
	private static final Class<? extends Annotation> annotationClass = TransactionDAO.class;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// TODO Auto-generated method stub

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		// TODO Auto-generated method stub

		ClassPathTransactionDAOScanner scanner = new ClassPathTransactionDAOScanner(registry);
		scanner.setAnnotationClass(annotationClass);

		scanner.registerFilters();
		scanner.scan(StringUtils.tokenizeToStringArray(getNamespace(),ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
		logger.info("registry " + annotationClass);
	}

}
