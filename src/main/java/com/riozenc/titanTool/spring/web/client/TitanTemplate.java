/**
 * Author : czy
 * Date : 2019年6月20日 上午8:25:53
 * Title : com.riozenc.titanTool.spring.web.client.TitanTemplate.java
 *
**/
package com.riozenc.titanTool.spring.web.client;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.riozenc.titanTool.common.json.utils.JSONUtil;

public class TitanTemplate extends RestTemplate {

	public <T> T post(String serverName, String url, HttpHeaders httpHeaders, Map<?, ?> params, Class<T> responseType)
			throws JsonParseException, JsonMappingException, IOException {

		String realUrl = "http://" + serverName + "/" + url;

		HttpEntity<Map<?, ?>> httpEntity = new HttpEntity<>(params, httpHeaders);
		String json = postForObject(realUrl, httpEntity, String.class);

		return JSONUtil.readValue(json, responseType);
	}
	
	public <T> T post(String serverName, String url, HttpHeaders httpHeaders, Map<?, ?> params, TypeReference<T> typeReference)
			throws JsonParseException, JsonMappingException, IOException {

		String realUrl = "http://" + serverName + "/" + url;

		HttpEntity<Map<?, ?>> httpEntity = new HttpEntity<>(params, httpHeaders);
		String json = postForObject(realUrl, httpEntity, String.class);

		return JSONUtil.readValue(json, typeReference);
	}

	public TitanCallback<Object> post1(String serverName, String url, HttpHeaders httpHeaders, Map<?, ?> params,
			Class<?> responseType) throws JsonParseException, JsonMappingException, IOException {

		String realUrl = "http://" + serverName + "/" + url;

		HttpEntity<Map<?, ?>> httpEntity = new HttpEntity<>(params, httpHeaders);
		String json = postForObject(realUrl, httpEntity, String.class);

		return new TitanCallback<Object>() {

			@Override
			public Object call() throws Exception {
				// TODO Auto-generated method stub

				return JSONUtil.readValue(json, responseType);
			}

			@Override
			public Object getBody() {
				// TODO Auto-generated method stub
				return json;
			}

		};
	}

	// restTemplate.getForObject("http://AUTH-CENTER/auth/extractToken?token=" +
	// token, String.class);

	// restTemplate.exchange("http://AUTH-DATA/auth-data/role/auth/table",HttpMethod.GET,
	// requestEntity, String.class);

	public interface TitanCallback<V> extends Callable<V> {

		public Object getBody();
	}

}
