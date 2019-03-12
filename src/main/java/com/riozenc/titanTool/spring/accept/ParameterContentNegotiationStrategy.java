/**
 *    Auth:riozenc
 *    Date:2018年6月21日 上午10:58:41
 *    Title:com.riozenc.quicktool.springmvc.strategy.ParameterContentNegotiationStrategy.java
 **/
package com.riozenc.titanTool.spring.accept;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;

public class ParameterContentNegotiationStrategy extends org.springframework.web.accept.ParameterContentNegotiationStrategy {
	private String defaultMediaTypeKey = "json";

	public ParameterContentNegotiationStrategy(Map<String, MediaType> mediaTypes) {
		super(mediaTypes);
		// TODO Auto-generated constructor stub
		setParameterName("output");
	}

	@Override
	protected String getMediaTypeKey(NativeWebRequest request) {
		// TODO Auto-generated method stub
		return super.getMediaTypeKey(request) == null ? defaultMediaTypeKey : super.getMediaTypeKey(request);
	}
}
