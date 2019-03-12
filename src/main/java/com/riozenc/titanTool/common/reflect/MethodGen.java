package com.riozenc.titanTool.common.reflect;

/**
 * get methodname of the set/get for field
 * 
 * @author Riozenc
 * 
 */
public class MethodGen {

	/**
	 * 
	 * @param type
	 * @param fieldName
	 * @return
	 */
	public static String generateMethodName(METHOD_TYPE type, String fieldName) {

		StringBuffer result = new StringBuffer();
		char[] temp = fieldName.toCharArray();
		if (temp.length > 1) {
			if (Character.isUpperCase(temp[1])) {
				return result.append(type).append(temp).toString();
			}
		}
		temp[0] = Character.toUpperCase(temp[0]);
		return result.append(type).append(temp).toString();
	}

	public enum METHOD_TYPE {
		set, get;
	}

}
