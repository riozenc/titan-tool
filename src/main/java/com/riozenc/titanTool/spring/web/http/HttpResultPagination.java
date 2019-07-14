/**
 * Author : czy
 * Date : 2019年7月9日 下午3:59:54
 * Title : com.riozenc.titanTool.spring.web.http.HttpResultPagination.java
 *
**/
package com.riozenc.titanTool.spring.web.http;

import java.util.List;

import com.riozenc.titanTool.mybatis.pagination.Page;

public class HttpResultPagination<T> {
	private Integer totalRow;
	private Integer pageCurrent;
	private List<T> list;

	public HttpResultPagination() {
	}

	public HttpResultPagination(Page page, List<T> list) {
		this.totalRow = page.getTotalRow();
		this.pageCurrent = page.getPageCurrent();
		this.list = list;
	}

	public HttpResultPagination(int totalRow, int pageCurrent, List<T> list) {
		this.totalRow = totalRow;
		this.pageCurrent = pageCurrent;
		this.list = list;
	}

	public Integer getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(Integer totalRow) {
		this.totalRow = totalRow;
	}

	public Integer getPageCurrent() {
		return pageCurrent;
	}

	public void setPageCurrent(Integer pageCurrent) {
		this.pageCurrent = pageCurrent;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
}
