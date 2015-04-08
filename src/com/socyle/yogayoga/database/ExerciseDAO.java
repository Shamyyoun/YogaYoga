package com.socyle.yogayoga.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.socyle.yogayoga.models.Exercise;
import com.socyle.yogayoga.models.Step;

public class ExerciseDAO {

	private SQLiteDatabase database;
	private DatabaseSQLiteHelper dbHelper;
	private String[] exerciseColumns = {
			DatabaseSQLiteHelper.EXERCISES_ID,
			DatabaseSQLiteHelper.EXERCISES_NAME,
			DatabaseSQLiteHelper.EXERCISES_ICON,
			};

	private String[] stepColumns = {
			DatabaseSQLiteHelper.STEPS_ID,
			DatabaseSQLiteHelper.STEPS_IMAGE,
			DatabaseSQLiteHelper.STEPS_DESC,
			DatabaseSQLiteHelper.STEPS_DURATION
			};

	public ExerciseDAO(Context context) {
		dbHelper = new DatabaseSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void add(Exercise exercise) {
		// add exercise
		ContentValues exerciseValues = new ContentValues();
		exerciseValues.put(DatabaseSQLiteHelper.EXERCISES_NAME,
				exercise.getName());
		exerciseValues.put(DatabaseSQLiteHelper.EXERCISES_ICON,
				exercise.getIconId());
		long exerciseId = database.insert(
				DatabaseSQLiteHelper.TABLE_EXERCISES, null, exerciseValues);

		// add steps
		ContentValues stepsValues = new ContentValues();
		for (int i = 0; i < exercise.getSteps().size(); i++) {
			Step step = exercise.getSteps().get(i);
			stepsValues.clear();
			stepsValues.put(DatabaseSQLiteHelper.STEPS_IMAGE, step.getImageId());
			stepsValues.put(DatabaseSQLiteHelper.STEPS_DESC, step.getDesc());
			stepsValues.put(DatabaseSQLiteHelper.STEPS_DURATION, step.getDuration());
			stepsValues.put(DatabaseSQLiteHelper.STEPS_EXERCISE_ID, exerciseId);
			database.insert(DatabaseSQLiteHelper.TABLE_STEPS, null,
					stepsValues);
		}
	}

	public List<Exercise> getAll() {
		List<Exercise> exercises = new ArrayList<Exercise>();

		Cursor cursor = database.query(
				DatabaseSQLiteHelper.TABLE_EXERCISES, exerciseColumns, null,
				null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Exercise exercise = cursorToExercise(cursor);
			exercises.add(exercise);
			cursor.moveToNext();
		}
		cursor.close();
		return exercises;
	}

	private Exercise cursorToExercise(Cursor cursor) {
		int id = cursor.getInt(cursor
				.getColumnIndex(DatabaseSQLiteHelper.EXERCISES_ID));
		String name = cursor.getString(cursor
				.getColumnIndex(DatabaseSQLiteHelper.EXERCISES_NAME));
		int icon = cursor.getInt(cursor
				.getColumnIndex(DatabaseSQLiteHelper.EXERCISES_ICON));
		List<Step> steps = getSteps(id);
		Exercise exercise = new Exercise(name, icon, steps);
		exercise.setId(id);
		return exercise;
	}

	private List<Step> getSteps(int exerciseId) {
		List<Step> steps = new ArrayList<Step>();

		Cursor cursor = database.query(DatabaseSQLiteHelper.TABLE_STEPS,
				stepColumns, DatabaseSQLiteHelper.STEPS_EXERCISE_ID + " = "
						+ exerciseId, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Step step = cursorToStep(cursor);
			steps.add(step);
			cursor.moveToNext();
		}
		cursor.close();
		return steps;
	}

	private Step cursorToStep(Cursor cursor) {
		int id = cursor.getInt(cursor
				.getColumnIndex(DatabaseSQLiteHelper.STEPS_ID));
		int image = cursor.getInt(cursor
				.getColumnIndex(DatabaseSQLiteHelper.STEPS_IMAGE));
		String desc = cursor.getString(cursor
				.getColumnIndex(DatabaseSQLiteHelper.STEPS_DESC));
		int duration = cursor.getInt(cursor
				.getColumnIndex(DatabaseSQLiteHelper.STEPS_DURATION));

		Step step = new Step(image, desc, duration);
		step.setId(id);
		return step;
	}

	public Exercise get(int id) {
		Cursor cursor = database.query(
				DatabaseSQLiteHelper.TABLE_EXERCISES, exerciseColumns,
				DatabaseSQLiteHelper.EXERCISES_ID + " = " + id, null,
				null, null, null);

		cursor.moveToFirst();
		Exercise exercise = null;
		while (!cursor.isAfterLast()) {
			exercise = cursorToExercise(cursor);
			cursor.moveToNext();
		}
		cursor.close();

		return exercise;
	}
	
	public int getCount() {
		String sql = "SELECT  * FROM " + DatabaseSQLiteHelper.TABLE_EXERCISES;
	    Cursor cursor = database.rawQuery(sql, null);
	    int count = cursor.getCount();
	    cursor.close();
	    return count;
	}
}
