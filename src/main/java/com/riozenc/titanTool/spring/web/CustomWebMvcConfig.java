/**
 *    Auth:riozenc
 *    Date:2019年3月12日 下午3:51:25
 *    Title:com.riozenc.titanTool.spring.web.CustomWebMvcConfig.java
 **/
package com.riozenc.titanTool.spring.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

import com.riozenc.titanTool.spring.accept.ContentNegotiationManagerFactoryBean;
import com.riozenc.titanTool.spring.context.SpringContextHolder;

/**
 * SpringContextHolder 
 * ContentNegotiationManagerFactoryBean
 * @author riozenc
 *
 */
public class CustomWebMvcConfig {
	@Bean
	public SpringContextHolder springContextHolder() {
		return new SpringContextHolder();
	}

	@Bean(name = "mvcContentNegotiationManager")
	public ContentNegotiationManagerFactoryBean contentNegotiationManagerFactoryBean() {
		ContentNegotiationManagerFactoryBean bean = new ContentNegotiationManagerFactoryBean();
		bean.setFavorPathExtension(false);
		bean.setFavorParameter(false);
		Map<String, MediaType> mediaTypes = new HashMap<String, MediaType>();
		mediaTypes.put("json", MediaType.APPLICATION_JSON_UTF8);
		mediaTypes.put("xml", MediaType.APPLICATION_XML);
		bean.addMediaTypes(mediaTypes);
		return bean;
	}
}
