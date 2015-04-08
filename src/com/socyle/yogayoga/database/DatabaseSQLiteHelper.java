package com.socyle.yogayoga.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseSQLiteHelper extends SQLiteOpenHelper {
	private Context context;

	// database info
	private static final String DATABASE_NAME = "yogayoga.db";
	private static final int DATABASE_VERSION = 6;

	// table exercises info
	public static final String TABLE_EXERCISES = "exercises";
	public static final String EXERCISES_ID = "_id";
	public static final String EXERCISES_NAME = "name";
	public static final String EXERCISES_ICON = "icon";

	// table steps info
	public static final String TABLE_STEPS = "steps";
	public static final String STEPS_ID = "_id";
	public static final String STEPS_IMAGE = "image";
	public static final String STEPS_DESC = "desc";
	public static final String STEPS_DURATION = "duration";
	public static final String STEPS_EXERCISE_ID = "ex_id";

	// table programs info
	public static final String TABLE_PROGRAMS = "programs";
	public static final String PROGRAMS_ID = "_id";
	public static final String PROGRAMS_TOTAL_TIME = "total_time";
	public static final String PROGRAMS_TRANSITION_TIME = "transition_time";

	// table pro_ex info
	public static final String TABLE_PROEX = "pro_ex";
	public static final String PROEX_ID = "_id";
	public static final String PROEX_PRO_ID = "program_id";
	public static final String PROEX_EX_ID = "exercise_id";
	public static final String PROEX_DONE = "done";

	// tables creation
	private static final String EXERCISES_CREATE = "CREATE TABLE "
			+ TABLE_EXERCISES + "(" + EXERCISES_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + EXERCISES_NAME
			+ " TEXT, " + EXERCISES_ICON + " INTEGER NOT NULL);";

	private static final String STEPS_CREATE = "CREATE TABLE " + TABLE_STEPS
			+ "(" + STEPS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ STEPS_IMAGE + " INTEGER NOT NULL, " + STEPS_DESC + " TEXT, "
			+ STEPS_DURATION + " INTEGER NOT NULL, " + STEPS_EXERCISE_ID
			+ " INTEGER NOT NULL, " + "FOREIGN KEY (" + STEPS_EXERCISE_ID
			+ ") REFERENCES " + TABLE_EXERCISES + "(" + EXERCISES_ID + "));";

	private static final String PROGRAMS_CREATE = "CREATE TABLE "
			+ TABLE_PROGRAMS + "("
			+ PROGRAMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ PROGRAMS_TOTAL_TIME + " INTEGER NOT NULL, "
			+ PROGRAMS_TRANSITION_TIME + " INTEGER DEFAULT 0);";

	private static final String PROEX_CREATE = "CREATE TABLE " + TABLE_PROEX
			+ "(" + PROEX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ PROEX_PRO_ID + " INTEGER NOT NULL, " + PROEX_EX_ID
			+ " INTEGER NOT NULL, " + PROEX_DONE + " INTEGER DEFAULT 0, "
			+ "FOREIGN KEY (" + PROEX_PRO_ID + ") REFERENCES " + TABLE_PROGRAMS
			+ "(" + PROGRAMS_ID + "), " + "FOREIGN KEY (" + PROEX_EX_ID
			+ ") REFERENCES " + TABLE_EXERCISES + "(" + EXERCISES_ID + ")"
			+ ")";

	public DatabaseSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// create tables
		database.execSQL(EXERCISES_CREATE);
		database.execSQL(STEPS_CREATE);
		database.execSQL(PROGRAMS_CREATE);
		database.execSQL(PROEX_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROEX);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRAMS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
		onCreate(db);
	}

}
