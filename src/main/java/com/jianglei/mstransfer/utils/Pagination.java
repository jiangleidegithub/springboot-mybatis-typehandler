package com.jianglei.mstransfer.utils;

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 * 
 * @author wuhao
 */
@SuppressWarnings({ "serial" })
public class Pagination implements Serializable {

	private Integer pageIndex; // 当前页码
	private Integer pageSize; // 每页大小
	private int totalRecordCnt; // 总记录数

	private List<?> dataList; // 结果数据列表

	@Override
	public String toString() {
		return "Pagination [pageIndex=" + pageIndex + ", pageSize=" + pageSize + ", totalRecordCnt=" + totalRecordCnt
				+ ", dataList=" + dataList + "]";
	}

	/**
	 * 总页数
	 * 
	 * @return
	 */
	public int getTotalPageCnt() {
		return (totalRecordCnt + pageSize - 1) / pageSize;
	}

	/**
	 * 分页查询起始记录
	 * 
	 * @return
	 */
	public int offset() {
		return (pageIndex - 1) * pageSize;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalRecordCnt() {
		return totalRecordCnt;
	}

	public void setTotalRecordCnt(int totalRecordCnt) {
		this.totalRecordCnt = totalRecordCnt;
	}

	public List<?> getDataList() {
		return dataList;
	}

	public void setDataList(List<?> dataList) {
		this.dataList = dataList;
	}


}
