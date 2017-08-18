package com.cn.dao.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据列表封装
 *
 * @author linp
 *
 * @param <T>
 */
public class Page<T> {

	/**
	 * 数据列表
	 */
	private List<T> list;

	/**
	 * 每页数据量
	 */
	private int pageSize;

	/**
	 * 当前页
	 */
	private int pageCurrent;

	/**
	 * 状态信息
	 */
	private String pageStatus = "";

	/**
	 * 数据总量
	 */
	private int total;

	/**
	 * 首页
	 *
	 */
	private int pageFirst = 0;

	/**
	 * 末页
	 */
	private int pageLast;

	/**
	 * 总页数
	 */
	private int pageTotal;

	/**
	 * 数据页列表
	 */
	private ArrayList<Integer> pageList;

	/**
	 * 构造函数
	 *
	 * @param listXmlString
	 * @param pageSize
	 * @param pageCurrent
	 * @param total
	 */
	public Page(List<T> list, int pageSize, int pageCurrent, int total) {
		this.setList(list);
		this.setPageSize(pageSize);
		this.setPageCurrent(pageCurrent);
		this.setTotal(total);

		/**
		 * 确定首页
		 */
		this.setPageFirst(0);

		/**
		 * 确定末页
		 */
		if (total % pageSize == 0) {
			this.setPageLast(total / pageSize - 1);
		} else {
			this.setPageLast(total / pageSize);
		}

		/**
		 * 确定当前页
		 */
		if (pageCurrent < 0) {
			this.pageCurrent = 0;
			this.pageStatus = "首页页码必须大于等于0";
		} else if (pageCurrent > this.pageLast) {
			this.pageCurrent = this.pageLast;
			this.pageStatus = "末页页码必须小于页码总数";
		} else {
			this.pageCurrent = pageCurrent;
		}

		/**
		 * 确定页面数列表，列出当前页附近的9个数据页
		 */
		int lastPageNumList = this.pageCurrent + 4;
		if (lastPageNumList > this.pageLast) {
			lastPageNumList = this.pageLast;
		}

		int firstPageNumList = lastPageNumList - 8;
		if (firstPageNumList < 0) {
			firstPageNumList = 0;
			lastPageNumList = 8;
		}

		this.pageList = new ArrayList<Integer>();
		for (int i = firstPageNumList; i <= lastPageNumList; ++i) {
			this.pageList.add(i);
		}

		/**
		 * 确定总页数
		 */
		this.pageTotal = pageLast - pageFirst + 1;
	}

	public List<T> getList() {
		return list;
	}

	public int getPageCurrent() {
		return pageCurrent;
	}

	public int getPageFirst() {
		return pageFirst;
	}

	public int getPageLast() {
		return pageLast;
	}

	public ArrayList<Integer> getPageList() {
		return pageList;
	}

	public int getPageSize() {
		return pageSize;
	}

	public String getPageStatus() {
		return pageStatus;
	}

	public int getPageTotal() {
		return pageTotal;
	}

	public int getTotal() {
		return total;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public void setPageCurrent(int pageCurrent) {
		this.pageCurrent = pageCurrent;
	}

	public void setPageFirst(int pageFirst) {
		this.pageFirst = pageFirst;
	}

	public void setPageLast(int pageLast) {
		this.pageLast = pageLast;
	}

	public void setPageList(ArrayList<Integer> pageList) {
		this.pageList = pageList;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setPageStatus(String pageStatus) {
		this.pageStatus = pageStatus;
	}

	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
