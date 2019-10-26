/**
 * Author : czy
 * Date : 2019年6月20日 上午8:25:53
 * Title : com.riozenc.titanTool.spring.web.client.TitanTemplate.java
 *
**/
package com.riozenc.titanTool.spring.web.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.riozenc.titanTool.common.json.utils.GsonUtils;
import com.riozenc.titanTool.common.json.utils.JSONUtil;

public class TitanTemplate {

	public RestTemplate restTemplate;

	public TitanTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public <T, V> T post(String serverName, String url, HttpHeaders httpHeaders, V params, Class<T> responseType)
			throws Exception {

		String realUrl = "http://" + serverName + "/" + url;

		HttpEntity<V> httpEntity = new HttpEntity<>(params, httpHeaders);

		return JSONUtil.readValue(http(serverName, realUrl, httpEntity), responseType);
	}

	public <T, V> T post(String serverName, String url, HttpHeaders httpHeaders, V params,
			TypeReference<T> typeReference) throws Exception {
		String realUrl = "http://" + serverName + "/" + url;

		HttpEntity<V> httpEntity = new HttpEntity<>(params, httpHeaders);

		String json = http(serverName, realUrl, httpEntity);
		return JSONUtil.readValue(json, typeReference);
	}

	public <T> T postJson(String serverName, String url, HttpHeaders httpHeaders, Map<?, ?> params, Class<T> clazz)
			throws Exception {
		if (httpHeaders == null) {
			httpHeaders = new HttpHeaders();
		}
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		return post(serverName, url, httpHeaders, params, clazz);
	}

	public <T> T postJson(String serverName, String url, HttpHeaders httpHeaders, Map<?, ?> params,
			TypeReference<T> typeReference) throws Exception {
		if (httpHeaders == null) {
			httpHeaders = new HttpHeaders();
		}
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		return post(serverName, url, httpHeaders, params, typeReference);
	}

	public <T> List<T> postJsonToList(String serverName, String url, HttpHeaders httpHeaders, Map<?, ?> params,
			Class<T> clazz) throws Exception {
		if (httpHeaders == null) {
			httpHeaders = new HttpHeaders();
		}
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		String realUrl = "http://" + serverName + "/" + url;

		HttpEntity<?> httpEntity = new HttpEntity<>(params, httpHeaders);
		Type typeOfT = TypeToken.getParameterized(List.class, clazz).getType();

		try {
			return GsonUtils.readValueToList(http(serverName, realUrl, httpEntity), typeOfT);
		} catch (JsonSyntaxException e) {
			return GsonUtils.readValueToList(parseJson(http(serverName, realUrl, httpEntity)), typeOfT);
		}
	}

	public <T> TitanCallback<T> postCallBack(String serverName, String url, HttpHeaders httpHeaders, Map<?, ?> params,
			Class<T> responseType) throws JsonParseException, JsonMappingException, IOException {

		String realUrl = "http://" + serverName + "/" + url;

		HttpEntity<Map<?, ?>> httpEntity = new HttpEntity<>(params, httpHeaders);
		String json = restTemplate.postForObject(realUrl, httpEntity, String.class);

		return new TitanCallback<T>() {

			@Override
			public T call() throws Exception {
				// TODO Auto-generated method stub

				return JSONUtil.readValue(json, responseType);
			}

			@Override
			public String getBody() {
				// TODO Auto-generated method stub
				return json;
			}

		};
	}

	private String http(String serverName, String realUrl, HttpEntity<?> httpEntity) throws Exception {
		try {
			String json = restTemplate.postForObject(realUrl, httpEntity, String.class);
			return json;
		} catch (Exception e) {
			// TODO: handle exception
			throw new Exception(serverName + "|" + realUrl + "服务执行失败，case:" + e);
		}
	}

	private String parseJson(String json) {
		JsonElement jsonElement = GsonUtils.readValue(json, JsonElement.class);
		if (jsonElement.isJsonObject()) {
			if (jsonElement.getAsJsonObject().get("statusCode").getAsInt() == 200
					&& jsonElement.getAsJsonObject().get("message") != null) {
				return jsonElement.getAsJsonObject().get("message").toString();
			}
		}
		return json;
	}

	public static void main(String[] args) {
//		String json = "{\"statusCode\":200,\"message\":[{\"id\":3,\"priceExecutionId\":4,\"priceName\":\"0.4KV居民生活用电\",\"ladderSn\":1,\"ladderValue\":170,\"price\":0.4834750,\"createDate\":\"2019-10-15T01:21:54.000+0000\",\"status\":0},{\"id\":4,\"priceExecutionId\":4,\"priceName\":\"0.4KV居民生活用电\",\"ladderSn\":2,\"ladderValue\":260,\"price\":0.5334750,\"createDate\":\"2019-10-15T01:05:45.000+0000\",\"status\":1},{\"id\":5,\"priceExecutionId\":4,\"priceName\":\"0.4KV居民生活用电\",\"ladderSn\":3,\"ladderValue\":-1,\"price\":0.7834750,\"createDate\":\"2019-10-15T01:06:37.000+0000\",\"status\":1}]}";
		String json = "{\"statusCode\":200,\"message\":[{\"id\":3,\"priceExecutionId\":4,\"priceName\":\"0.4KV居民生活用电\",\"ladderSn\":1,\"ladderValue\":170,\"price\":0.4834750,\"createDate\":\"2019-10-15T01:21:54.000+0000\",\"status\":0},{\"id\":4,\"priceExecutionId\":4,\"priceName\":\"0.4KV居民生活用电\",\"ladderSn\":2,\"ladderValue\":260,\"price\":0.5334750,\"createDate\":\"2019-10-15T01:05:45.000+0000\",\"status\":1},{\"id\":5,\"priceExecutionId\":4,\"priceName\":\"0.4KV居民生活用电\",\"ladderSn\":3,\"ladderValue\":-1,\"price\":0.7834750,\"createDate\":\"2019-10-15T01:06:37.000+0000\",\"status\":1}]}";

		JsonElement jsonElement = GsonUtils.readValue(json, JsonElement.class);

		if (jsonElement.isJsonObject()) {
			if (jsonElement.getAsJsonObject().get("statusCode").getAsInt() == 200
					&& jsonElement.getAsJsonObject().get("message") != null) {
				System.out.println(jsonElement.getAsJsonObject().get("message").toString());
				return;
			}
		}

		System.out.println(json);

	}

	public interface TitanCallback<V> extends Callable<V> {

		public Object getBody();
	}

}
