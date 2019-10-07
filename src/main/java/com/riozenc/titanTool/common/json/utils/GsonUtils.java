/**
 * Author : czy
 * Date : 2019年7月4日 上午10:35:42
 * Title : com.riozenc.titanTool.common.json.utils.GsonUtils.java
 *
**/
package com.riozenc.titanTool.common.json.utils;

import java.lang.reflect.Type;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.riozenc.titanTool.common.json.annotation.IgnorRead;
import com.riozenc.titanTool.common.json.annotation.IgnorWrite;

public class GsonUtils {

	private final static Gson GSON = new Gson();

	private final static Gson IGNORE_NULL_GOSN = new GsonBuilder()// 建造者模式设置不同的配置
			.disableHtmlEscaping()// 防止对网址乱码 忽略对特殊字符的转换
			.setDateFormat("yyyy-MM-dd HH:mm:ss") // 设置日期的格式
			.create();

	private final static Gson ignorReadGson = new GsonBuilder()
			.addSerializationExclusionStrategy(new ExclusionStrategy() {

				@Override
				public boolean shouldSkipField(FieldAttributes f) {
					IgnorRead ignorRead = f.getAnnotation(IgnorRead.class);
					if (ignorRead != null) {
						return true;
					}
					return false;
				}

				@Override
				public boolean shouldSkipClass(Class<?> clazz) {
					// TODO Auto-generated method stub
					return false;
				}
			}).create();

	private final static Gson ignorWriteGson = new GsonBuilder()
			.addDeserializationExclusionStrategy(new ExclusionStrategy() {

				@Override
				public boolean shouldSkipField(FieldAttributes f) {
					// TODO Auto-generated method stub
					IgnorWrite ignorWrite = f.getAnnotation(IgnorWrite.class);
					if (ignorWrite != null) {
						return true;
					}
					return false;
				}

				@Override
				public boolean shouldSkipClass(Class<?> clazz) {
					// TODO Auto-generated method stub
					return false;
				}
			}).create();

	public static Gson getGson() {
		return GSON;
	}

	public static Gson getIgnorReadGson() {
		return ignorReadGson;
	}

	public static Gson getIgnorWriteGson() {
		return ignorWriteGson;
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

	public static <T> T readValueToList(String json, Type typeOfT) {

		return GSON.fromJson(json, typeOfT);
	}

}
