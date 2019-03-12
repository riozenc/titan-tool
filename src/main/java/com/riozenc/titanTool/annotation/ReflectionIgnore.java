/**
 * @Project:quicktool
 * @Title:ReflectionIgnore.java
 * @Author:Riozenc
 * @Datetime:2017年4月2日 下午1:40:48
 * 
 */
package com.riozenc.titanTool.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReflectionIgnore {

}
