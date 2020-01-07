/**
 *    Auth:riozenc
 *    Date:2018年7月26日 上午8:43:51
 *    Title:cis.web.result.HttpResult.java
 **/
package com.riozenc.titanTool.spring.web.http;

public class HttpResult<T> {
	public static final int SUCCESS = 200;
	public static final int ERROR = 300;

	private Integer statusCode;

	private String message;

	private T resultData;

	public HttpResult() {
	}

	public HttpResult(Integer statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
		this.resultData = null;
	}
	
	public HttpResult(Integer statusCode, T resultData) {
		this.statusCode = statusCode;
		this.message = null;
		this.resultData = resultData;
	}

	public HttpResult(Integer statusCode, String message, T resultData) {
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getResultData() {
		return resultData;
	}

	public void setResultData(T resultData) {
		this.resultData = resultData;
	}

}
