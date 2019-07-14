/**
 * Author : czy
 * Date : 2019年7月4日 上午10:35:42
 * Title : com.riozenc.titanTool.common.json.utils.GsonUtils.java
 *
**/
package com.riozenc.titanTool.common.json.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {

	private final static Gson GSON = new Gson();

	private final static Gson IGNORE_NULL_GOSN = new GsonBuilder()// 建造者模式设置不同的配置
			.disableHtmlEscaping()// 防止对网址乱码 忽略对特殊字符的转换
			.setDateFormat("yyyy-MM-dd HH:mm:ss") // 设置日期的格式
			.create();

	public static Gson getGson() {
		return GSON;
	}

	public static String toJson(Object object) {
		return GSON.toJson(object);
	}

	public static String toJsonIgnoreNull(Object object) {
		return IGNORE_NULL_GOSN.toJson(object);
	}

	public static <T> T readValue(String json, Class<T> clazz) {
		return GSON.fromJson(json, clazz);
	}
}
