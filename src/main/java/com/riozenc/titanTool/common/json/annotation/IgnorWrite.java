/**
 * Author : czy
 * Date : 2019年8月28日 下午4:37:00
 * Title : com.riozenc.titanTool.common.json.annotation.IgnorWrite.java
 *
**/
package com.riozenc.titanTool.common.json.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IgnorWrite {

}
