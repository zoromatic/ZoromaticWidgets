package com.zoromatic.widgets;

public class RowItem {
	private int imageId;
	//private String title;
	private String desc;
	private boolean arrow;

	public RowItem(int imageId, /*String title, */String desc, boolean arrow) {
		this.imageId = imageId;
		//this.title = title;
		this.desc = desc;
		this.arrow = arrow;
	}
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public boolean isArrow() {
		return arrow;
	}
	public void setArrow(boolean arrow) {
		this.arrow = arrow;
	}
//	public String getTitle() {
//		return title;
//	}
//	public void setTitle(String title) {
//		this.title = title;
//	}
//	@Override
//	public String toString() {
//		return title + "\n" + desc;
//	}   
}
