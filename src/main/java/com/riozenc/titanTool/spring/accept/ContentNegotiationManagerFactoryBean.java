/**
 *    Auth:riozenc
 *    Date:2018年6月19日 下午6:29:34
 *    Title:com.riozenc.quicktool.springmvc.strategy.ContentNegotiationManagerFactoryBean.java
 **/
package com.riozenc.titanTool.spring.accept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

public class ContentNegotiationManagerFactoryBean
		extends org.springframework.web.accept.ContentNegotiationManagerFactoryBean {
	private Map<String, MediaType> mediaTypes = new HashMap<String, MediaType>();
	private ContentNegotiationManager contentNegotiationManager;

	public void addMediaTypes(Map<String, MediaType> mediaTypes) {
		if (mediaTypes != null) {
			this.mediaTypes.putAll(mediaTypes);
		}
	}

	@Override
	public void afterPropertiesSet() {

		List<ContentNegotiationStrategy> strategies = new ArrayList<ContentNegotiationStrategy>();

		ParameterContentNegotiationStrategy strategy = new ParameterContentNegotiationStrategy(this.mediaTypes);

		strategies.add(strategy);
		strategies.add(new HeaderContentNegotiationStrategy());

		contentNegotiationManager = new ContentNegotiationManager(strategies);
	}

	@Override
	public ContentNegotiationManager getObject() {
		// TODO Auto-generated method stub
		return contentNegotiationManager;
	}
}
