package com.socyle.yogayoga.models;

import java.util.List;

public class ProgramExercise extends Exercise {
	private boolean done;
	
	public ProgramExercise(String name, int icon, List<Step> steps, boolean done) {
		super(name, icon, steps);
		this.done = done;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}
	
	public boolean isDone() {
		return done;
	}
}
