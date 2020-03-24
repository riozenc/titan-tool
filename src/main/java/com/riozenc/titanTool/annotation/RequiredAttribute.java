/**
 * Author : chizf
 * Date : 2020年3月11日 下午4:25:09
 * Title : com.riozenc.titanTool.annotation.RequiredAttribute.java
 *
**/
package com.riozenc.titanTool.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 必要属性
 * @author czy
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredAttribute {

}
