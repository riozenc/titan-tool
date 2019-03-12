/**
 *	
 * @Title:JSONUtil.java
 * @author Riozen
 *	@date 2013-12-25 下午2:08:06
 *	
 */
package com.riozenc.titanTool.common.json.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {

	private static final Log LOG = LogFactory.getLog(JSONUtil.class);

	/**
	 * 对象输出json
	 * 
	 * @param object
	 * @return
	 */
	public static String toJsonString(Object object) {
		return toJsonString(object, null, false);
	}

	/**
	 * 
	 * @param object
	 * @param datePattern
	 * @return
	 */
	public static String toJsonString(Object object, String datePattern) {
		return toJsonString(object, datePattern, false);
	}

	/**
	 * 
	 * @param object       对象
	 * @param datePattern  日期格式eg:'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd'
	 * @param isIgnoreNull 是否忽略domain中注解的属性
	 * @return
	 */
	public static String toJsonString(Object object, String datePattern, boolean isIgnoreNull) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			if (null != datePattern) {
				objectMapper.setDateFormat(new SimpleDateFormat(datePattern));
			}

			if (isIgnoreNull) {
				// 配置mapper忽略空属性
				objectMapper.setSerializationInclusion(Include.NON_EMPTY);
			}
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			LOG.info("write to json string error:" + object, e);
			return null;
		}
	}

	/**
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T readValue(String json, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}

	public static <T> T readValue(String json, TypeReference<T> typeReference)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, typeReference);
	}
}
