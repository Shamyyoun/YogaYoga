package com.socyle.yogayoga.models;

import java.io.Serializable;
import java.util.List;

public class Exercise implements Serializable{
	private int id;
	private String name;
	private int iconId;
	private int totalDuration;
	private List<Step> steps;

	public Exercise(String name, int iconId, List<Step> steps) {
		this.iconId = iconId;
		this.name = name;
		this.steps = steps;
		
		// calc totalDuration
		for (Step step : steps) {
			totalDuration += step.getDuration();
		}
	}
	
	public int getCurrentTime(int frameNo) {
		int currentTime = 0;
		for (int i = 0; i <= frameNo; i++) {
			currentTime += steps.get(i).getDuration();
		}
		
		return currentTime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getIconId() {
		return iconId;
	}

	public List<Step> getSteps() {
		return steps;
	}
	
	public int getTotalDuration() {
		return totalDuration;
	}
}
