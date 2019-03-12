/**
 * Title:AbstractRegistryPostProcessor.java
 * Author:czy
 * Datetime:2016年11月11日 上午10:26:08
 */
package com.riozenc.titanTool.spring.transaction.registry;

public abstract class AbstractRegistryPostProcessor {
	private String namespace = "namespace";

	protected String getNamespace() {
		return namespace;
	}
}
