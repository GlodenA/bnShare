package com.ipu.server.util;



public class Pagination {
	//当前显示多少行
	private int size;
	//展示多少分页
	private int showCount;
	public Pagination(int size,int showCount){
		this.showCount=showCount;
		this.size=size;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getShowCount() {
		return showCount;
	}
	public void setShowCount(int showCount) {
		this.showCount = showCount;
	}
}