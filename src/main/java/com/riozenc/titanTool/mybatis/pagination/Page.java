package com.riozenc.titanTool.mybatis.pagination;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.riozenc.titanTool.properties.Global;

public class Page {

	@JsonProperty(access = Access.WRITE_ONLY)
	private int totalRow;// 总条数
	@JsonProperty(access = Access.WRITE_ONLY)
	private int pageCurrent = 1; // 当前页
	@JsonProperty(access = Access.WRITE_ONLY)
	private int pageSize = Integer.valueOf(Global.getConfig("page.pageSize")); // 页面大小，设置为“-1”表示不进行分页（分页无效）
	@JsonProperty(access = Access.WRITE_ONLY)
	private String dbName;

	public int getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}

	public int getPageCurrent() {
		return pageCurrent;
	}

	public void setPageCurrent(int pageCurrent) {
		this.pageCurrent = pageCurrent;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * 获取 Hibernate FirstResult
	 */
	@JsonIgnore
	public int getFirstResult() {
		int firstResult = (getPageCurrent() - 1) * getPageSize();
		if (firstResult >= getTotalRow()) {
			firstResult = 0;
		}
		return firstResult;
	}

	/**
	 * 获取 Hibernate MaxResults
	 */
	@JsonIgnore
	public int getMaxResults() {
		return getPageSize();
	}

}
