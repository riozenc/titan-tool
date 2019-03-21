/**
 *    Auth:riozenc
 *    Date:2019年3月19日 上午10:40:22
 *    Title:com.riozenc.titanTool.annotation.LogSupportAop.java
 **/
package com.riozenc.titanTool.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AfterAopSupport {
	String method();// 方法

	Class<?>[] parameterTypes();// 参数类型

}
