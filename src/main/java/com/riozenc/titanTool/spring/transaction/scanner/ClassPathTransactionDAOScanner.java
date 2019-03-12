/**
 * Title:ClassPathMapperScanner.java
 * Author:czy
 * Datetime:2016年11月9日 下午12:11:14
 */
package com.riozenc.titanTool.spring.transaction.scanner;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import com.riozenc.titanTool.spring.transaction.bean.TransactionServiceFactoryBean;



public class ClassPathTransactionDAOScanner extends ClassPathBeanDefinitionScanner {

	private Class<? extends Annotation> annotationClass;

	public ClassPathTransactionDAOScanner(BeanDefinitionRegistry registry) {
		super(registry, false);
		// TODO Auto-generated constructor stub
	}

	public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

		if (beanDefinitions.isEmpty()) {
			logger.warn("No Transaction DAO was found in '" + Arrays.toString(basePackages)
					+ "' package. Please check your configuration.");
		} else {
			processBeanDefinitions(beanDefinitions);
		}

		return beanDefinitions;
	}

	/**
	 * Configures parent scanner to search for the right interfaces. It can
	 * search for all interfaces or just for those that extends a
	 * markerInterface or/and those annotated with the annotationClass
	 */
	public void registerFilters() {
		boolean acceptAllInterfaces = true;

		// if specified, use the given annotation and / or marker interface
		if (this.annotationClass != null) {
			addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
			acceptAllInterfaces = false;
		}

		if (acceptAllInterfaces) {
			// default include filter that accepts all classes
			addIncludeFilter(new TypeFilter() {
				@Override
				public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
						throws IOException {
					return true;
				}
			});
		}

		// exclude package-info.java
		addExcludeFilter(new TypeFilter() {
			@Override
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
					throws IOException {
				String className = metadataReader.getClassMetadata().getClassName();
				return className.endsWith("package-info");
			}
		});
	}

	private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
		for (BeanDefinitionHolder holder : beanDefinitions) {
			TransactionServiceFactoryBean.getDefinitionHolderMap().put(holder.getBeanName(), holder);
		}
	}

}
