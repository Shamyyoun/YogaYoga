package com.socyle.yogayoga.models;

import java.io.Serializable;
import java.util.List;

public class Program implements Serializable {
	private int id;
	private int totalDuration;
	private int progress;
	private int transitionTime;
	private int totalTime;
	private List<ProgramExercise> exercises;

	public Program(List<ProgramExercise> exercises) {
		this.exercises = exercises;

		// calc progress
		int done = 0;
		for (ProgramExercise exercise : exercises) {
			if (exercise.isDone()) {
				done++;
			}
		}
		progress = (int) (((float) done / exercises.size()) * 100);

		// calc total duration
		for (ProgramExercise exercise : exercises) {
			totalDuration += exercise.getTotalDuration();
		}
	}

	public ProgramExercise getNextExercise() {
		int position = getNextExercisePosition();
		if (position != -1) {
			return exercises.get(position);
		} else {
			return null;
		}
	}

	public int getNextExercisePosition() {
		for (int i = 0; i < exercises.size(); i++) {
			if (!exercises.get(i).isDone()) {
				return i;
			}
		}
		return -1;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getTotalDuration() {
		return totalDuration;
	}

	public int getProgress() {
		return progress;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	
	public int getTotalTime() {
		return totalTime;
	}

	public void setTransitionTime(int transitionTime) {
		this.transitionTime = transitionTime;
	}

	public int getTransitionTime() {
		return transitionTime;
	}

	public List<ProgramExercise> getExercises() {
		return exercises;
	}
}
