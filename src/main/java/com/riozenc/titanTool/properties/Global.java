
package com.riozenc.titanTool.properties;

import java.util.HashMap;
import java.util.Map;

public class Global {

	/**
	 * 当前对象实例
	 */
	private static Global global = new Global();

	/**
	 * 保存全局属性值
	 */
	private static Map<String, String> map = new HashMap<String, String>();

	/**
	 * 属性文件加载对象
	 */
	private static PropertiesLoader loader = new PropertiesLoader("config.properties");

	/**
	 * 获取当前对象实例
	 */
	public static Global getInstance() {
		return global;
	}

	/**
	 * 获取配置
	 * 
	 * @see ${fns:getConfig('adminPath')}
	 */
	public static String getConfig(String key) {
		return getConfig(key, null);
	}
	
	/**
	 * 获取配置
	 * 
	 * @see ${fns:getConfig('adminPath')}
	 */
	public static String getConfig(String key,String defaultValue) {
		String value = map.get(key);
		if (value == null) {
			value = loader.getProperty(key);
			map.put(key, value != null ? value : "");
		}
		return value==null?defaultValue:value;
	}

	/**
	 * 获取配置
	 * 
	 * @see ${fns:getConfig('adminPath')}
	 */
	public static Map<String, String> getConfigs(String key) {
		Map<String, String> map = new HashMap<String, String>();

		for (String temp : loader.getProperties().stringPropertyNames()) {
			if (temp.indexOf(key) > -1) {
				map.put(temp, getConfig(temp));
			}
		}
		return map;
	}

}
