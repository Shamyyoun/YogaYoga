package com.socyle.yogayoga.models;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;

import com.socyle.yogayoga.R;
import com.socyle.yogayoga.database.ExerciseDAO;

public class Exercises {
	public static void addAll(Context context) {
		// get exercises count
		ExerciseDAO exerciseDAO = new ExerciseDAO(context);
		exerciseDAO.open();
		int count = exerciseDAO.getCount();
		
		if (count == 0) {
			// show progress dialog
			ProgressDialog dialog = new ProgressDialog(context);
			dialog.setTitle("Please wait...");
			dialog.setMessage("Appears for the first time only");
			dialog.show();

			// then add to the DB
			for (int i = 0; i < 10; i++) {
				Step step = new Step(R.drawable.ex1_01, "desc desc desc desc desc " + i, 2);
				Step step2 = new Step(R.drawable.ex1_02, "desc desc desc desc desc " + i, 3);
				Step step3 = new Step(R.drawable.ex1_03, "desc desc desc desc desc " + i, 4);
				Step step4 = new Step(R.drawable.ex1_04, "desc desc desc desc desc " + i, 3);
				List<Step> steps = new ArrayList<Step>();
				steps.add(step);
				steps.add(step2);
				steps.add(step3);
				steps.add(step4);
				Exercise exercise = new Exercise("Exercise " + i, R.drawable.poses_but1, steps);
				exerciseDAO.add(exercise);
			}
			
			dialog.dismiss();
		}
		
		exerciseDAO.close();
	}
}
