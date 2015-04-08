package com.socyle.yogayoga.models;

import java.io.Serializable;

public class Step implements Serializable {
	private int id;
	private int imageId;
	private String desc;
	private int duration;

	public Step(int imageId, String desc, int duration) {
		this.imageId = imageId;
		this.desc = desc;
		this.duration = duration;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public int getImageId() {
		return imageId;
	}

	public String getDesc() {
		return desc;
	}
	
	public int getDuration() {
		return duration;
	}
}
