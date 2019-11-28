/**
 *    Auth:riozenc
 *    Date:2018年7月26日 上午8:43:51
 *    Title:cis.web.result.HttpResult.java
 **/
package com.riozenc.titanTool.spring.web.http;

public class HttpResult {
	public static final int SUCCESS = 200;
	public static final int ERROR = 300;

	private Integer statusCode;

	private Object message;

	private Object resultData;

	public HttpResult() {
	}

	public HttpResult(Integer statusCode, Object message) {
		this.statusCode = statusCode;
		this.message = message;
		this.resultData = null;
	}

	public HttpResult(Integer statusCode, Object message, Object resultData) {
		this.statusCode = statusCode;
		this.message = message;
		this.resultData = resultData;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public Object getResultData() {
		return resultData;
	}

	public void setResultData(Object resultData) {
		this.resultData = resultData;
	}

}
