/**
 * Title:MybatisDAO.java
 * Author:czy
 * Datetime:2016年11月1日 下午3:22:34
 */
package com.riozenc.titanTool.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MybatisDAO {

}
