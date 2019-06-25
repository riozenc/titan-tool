/**
 * Author : czy
 * Date : 2019年6月20日 上午10:28:12
 * Title : com.riozenc.titanTool.spring.web.client.RestObject.java
 *
**/
package com.riozenc.titanTool.spring.web.client;

import com.google.gson.JsonElement;

public class RestObject {
	private static final int SUCCESS = 200;
	private Integer status;
	private String message;
	private JsonElement data;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return this.status == SUCCESS;
	}

}
