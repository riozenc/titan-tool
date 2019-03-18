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

	public HttpResult(Integer statusCode, Object message) {
		this.statusCode = statusCode;
		this.message = message;
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
}
