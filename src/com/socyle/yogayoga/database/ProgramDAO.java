package com.socyle.yogayoga.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.socyle.yogayoga.models.Exercise;
import com.socyle.yogayoga.models.Program;
import com.socyle.yogayoga.models.ProgramExercise;

public class ProgramDAO {
	private SQLiteDatabase database;
	private DatabaseSQLiteHelper dbHelper;
	private Context context;
	private String[] programColumns = { DatabaseSQLiteHelper.PROGRAMS_ID,
			DatabaseSQLiteHelper.PROGRAMS_TOTAL_TIME,
			DatabaseSQLiteHelper.PROGRAMS_TRANSITION_TIME };

	private String[] proexColumns = { DatabaseSQLiteHelper.PROEX_ID,
			DatabaseSQLiteHelper.PROEX_PRO_ID,
			DatabaseSQLiteHelper.PROEX_EX_ID, DatabaseSQLiteHelper.PROEX_DONE };

	public ProgramDAO(Context context) {
		dbHelper = new DatabaseSQLiteHelper(context);
		this.context = context;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void add(Program program) {
		// add program
		ContentValues programValues = new ContentValues();
		programValues.put(DatabaseSQLiteHelper.PROGRAMS_TOTAL_TIME,
				program.getTotalTime());
		programValues.put(DatabaseSQLiteHelper.PROGRAMS_TRANSITION_TIME,
				program.getTransitionTime());
		long programId = database.insert(DatabaseSQLiteHelper.TABLE_PROGRAMS,
				null, programValues);

		// add exercise IDs
		ContentValues proexValues = new ContentValues();
		for (int i = 0; i < program.getExercises().size(); i++) {
			ProgramExercise exercise = program.getExercises().get(i);
			proexValues.clear();
			proexValues.put(DatabaseSQLiteHelper.PROEX_PRO_ID, programId);
			proexValues.put(DatabaseSQLiteHelper.PROEX_EX_ID, exercise.getId());
			proexValues.put(DatabaseSQLiteHelper.PROEX_DONE,
					exercise.isDone() ? 1 : 0);
			database.insert(DatabaseSQLiteHelper.TABLE_PROEX, null, proexValues);
		}
	}

	public void update(Program program) {
		// update total_time and transition_time
		ContentValues values = new ContentValues();
		values.put(DatabaseSQLiteHelper.PROGRAMS_TOTAL_TIME,
				program.getTotalTime());
		values.put(DatabaseSQLiteHelper.PROGRAMS_TRANSITION_TIME,
				program.getTransitionTime());
		database.update(DatabaseSQLiteHelper.TABLE_PROGRAMS, values,
				DatabaseSQLiteHelper.PROGRAMS_ID + "=" + program.getId(), null);

		// update exercises done
		List<ProgramExercise> exercises = program.getExercises();
		for (ProgramExercise programExercise : exercises) {
			updateExerciseInProgram(programExercise, program.getId());
		}
	}

	public void delete(int programId) {
		// delete from proex
		database.delete(DatabaseSQLiteHelper.TABLE_PROEX,
				DatabaseSQLiteHelper.PROEX_PRO_ID + " = " + programId, null);

		// then delete from programs
		database.delete(DatabaseSQLiteHelper.TABLE_PROGRAMS,
				DatabaseSQLiteHelper.PROGRAMS_ID + " = " + programId, null);
	}

	public List<Program> getAll() {
		List<Program> programs = new ArrayList<Program>();

		Cursor cursor = database.query(DatabaseSQLiteHelper.TABLE_PROGRAMS,
				programColumns, null, null, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			Program program = cursorToProgram(cursor);
			programs.add(program);
			cursor.moveToNext();
		}
		cursor.close();
		return programs;
	}

	public Program get(int id) {
		Cursor cursor = database.query(DatabaseSQLiteHelper.TABLE_PROGRAMS,
				programColumns, DatabaseSQLiteHelper.PROGRAMS_ID + " = " + id,
				null, null, null, null);

		cursor.moveToFirst();
		Program program = null;
		while (!cursor.isAfterLast()) {
			program = cursorToProgram(cursor);
			cursor.moveToNext();
		}
		cursor.close();

		return program;
	}

	private Program cursorToProgram(Cursor cursor) {
		int programId = cursor.getInt(cursor
				.getColumnIndex(DatabaseSQLiteHelper.PROGRAMS_ID));
		int totalTime = cursor.getInt(cursor
				.getColumnIndex(DatabaseSQLiteHelper.PROGRAMS_TOTAL_TIME));
		int transitionTime = cursor.getInt(cursor
				.getColumnIndex(DatabaseSQLiteHelper.PROGRAMS_TRANSITION_TIME));
		List<ProgramExercise> exercises = getAllExercisesInProgram(programId);
		Program program = new Program(exercises);
		program.setId(programId);
		program.setTotalTime(totalTime);
		program.setTransitionTime(transitionTime);
		return program;
	}

	public List<ProgramExercise> getAllExercisesInProgram(int programId) {
		// get IDs and done first
		int[][] exercisesData;
		Cursor cursor = database.query(DatabaseSQLiteHelper.TABLE_PROEX,
				proexColumns, DatabaseSQLiteHelper.PROEX_PRO_ID + " = "
						+ programId, null, null, null, null);

		exercisesData = new int[cursor.getCount()][2];
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int exerciseId = cursor.getInt(cursor
					.getColumnIndex(DatabaseSQLiteHelper.PROEX_EX_ID));
			int done = cursor.getInt(cursor
					.getColumnIndex(DatabaseSQLiteHelper.PROEX_DONE));
			exercisesData[cursor.getPosition()][0] = exerciseId;
			exercisesData[cursor.getPosition()][1] = done;
			cursor.moveToNext();
		}
		cursor.close();

		// get remain exercise data from exercise table
		List<ProgramExercise> programExercises = new ArrayList<ProgramExercise>();
		ExerciseDAO exerciseDAO = new ExerciseDAO(context);
		exerciseDAO.open();
		for (int i = 0; i < exercisesData.length; i++) {
			Exercise exercise = exerciseDAO.get(exercisesData[i][0]);
			ProgramExercise programExercise = new ProgramExercise(
					exercise.getName(), exercise.getIconId(),
					exercise.getSteps(), (exercisesData[i][1] == 1) ? true
							: false);
			programExercise.setId(exercisesData[i][0]);
			programExercises.add(programExercise);
		}
		exerciseDAO.close();

		return programExercises;
	}

	public ProgramExercise getProgramExerciseInProgram(int exerciseId,
			int programId) {
		Cursor cursor = database.query(DatabaseSQLiteHelper.TABLE_PROEX,
				proexColumns, DatabaseSQLiteHelper.PROEX_PRO_ID + " = "
						+ programId + " AND "
						+ DatabaseSQLiteHelper.PROEX_EX_ID + "=" + exerciseId,
				null, null, null, null);

		cursor.moveToFirst();
		boolean done = false;
		ProgramExercise programExercise = null;
		ExerciseDAO exerciseDAO = new ExerciseDAO(context);
		exerciseDAO.open();
		while (!cursor.isAfterLast()) {
			done = (cursor.getInt(cursor
					.getColumnIndex(DatabaseSQLiteHelper.PROEX_DONE)) == 1) ? true
					: false;

			// get remain exercise data from exercise table
			Exercise exercise = exerciseDAO.get(exerciseId);
			programExercise = new ProgramExercise(exercise.getName(),
					exercise.getIconId(), exercise.getSteps(), done);
			programExercise.setId(exerciseId);

			cursor.moveToNext();
		}
		exerciseDAO.close();
		cursor.close();
		return programExercise;
	}

	public boolean addExerciseToProgram(Exercise exercise, int programId) {
		// check if program exists or not
		Program program = get(programId);
		// check exercise exists in program or not
		ProgramExercise tempExercise = getProgramExerciseInProgram(
				exercise.getId(), programId);
		if (program != null && tempExercise == null) {
			// add it
			ContentValues values = new ContentValues();
			values.put(DatabaseSQLiteHelper.PROEX_PRO_ID, programId);
			values.put(DatabaseSQLiteHelper.PROEX_EX_ID, exercise.getId());
			values.put(DatabaseSQLiteHelper.PROEX_DONE, 0);

			database.insert(DatabaseSQLiteHelper.TABLE_PROEX, null, values);
			return true;
		} else {
			return false;
		}
	}

	public boolean updateExerciseInProgram(ProgramExercise exercise,
			int programId) {
		ContentValues values = new ContentValues();
		values.put(DatabaseSQLiteHelper.PROEX_DONE, exercise.isDone() ? 1 : 0);
		int rows = database.update(
				DatabaseSQLiteHelper.TABLE_PROEX,
				values,
				DatabaseSQLiteHelper.PROEX_PRO_ID + "=" + programId + " AND "
						+ DatabaseSQLiteHelper.PROEX_EX_ID + "="
						+ exercise.getId(), null);

		if (rows != 0) {
			return true;
		} else {
			return false;
		}
	}

	public void deleteExerciseFromProgram(int exerciseId, int programId) {
		database.delete(
				DatabaseSQLiteHelper.TABLE_PROEX,
				DatabaseSQLiteHelper.PROEX_EX_ID + " = " + exerciseId + " AND "
						+ DatabaseSQLiteHelper.PROEX_PRO_ID + " = " + programId,
				null);
	}

	public int getCount() {
		String sql = "SELECT  * FROM " + DatabaseSQLiteHelper.TABLE_PROGRAMS;
		Cursor cursor = database.rawQuery(sql, null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}
}
