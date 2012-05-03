package by.bsuir.scheduler.model;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

class DBHelper extends SQLiteOpenHelper {
	private long mTotalTime = 0;
	
	private int count;
	
	private static final String DATABASE_NAME = "scheduledb";
	private static final int DATABASE_VERSION = 3;
	private static final String SCHEDULE_TABLE_NAME = "schedule";
	private static final String NOTE_TABLE_NAME = "note";
	private static final String SUBJECT_TABLE_NAME = "subject";
	private static final String TIME_TABLE_NAME = "time";
	private static final String SUBJECT_TYPE_TABLE_NAME = "subject_type";
	private static final String DAY_TABLE_NAME = "day";
	private static final String TEACHER_TABLE_NAME = "teacher";
	private static final String[] DAYS = {"воскресение","понедельник", "вторник", "среда", "четверг", "пятница", "суббота"};
	protected static final String[] SUBJECT_TYPES = {"", "лекция", "практическое занятие", "лабораторная работа", "курсовое проектирование"};
	private static final String SCHEDULE_VIEW_NAME = "schedule_view";
	private static final String TAG = "DBHelper";
	private static final String DATE_FORMAT = "dd.MM.yyyy";
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	//	context.deleteDatabase(DATABASE_NAME);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("PRAGMA foreign_keys=ON;");
		
		/*=====Subject=====*/
		String sql = "CREATE TABLE " + SUBJECT_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumns.NAME + " TEXT);";
		db.execSQL(sql);
		Log.i(TAG, sql);
		
		/*=====Day=====*/
		/*sql = "CREATE TABLE " + DAY_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumns.NAME + " TEXT);";
		db.execSQL(sql);
		for(String day : DAYS){
			sql = "INSERT INTO " + DAY_TABLE_NAME + " (" + DBColumns.NAME+ ") VALUES ('" + day + "');";
			db.execSQL(sql);
		}
		Log.i(TAG, sql);
		*/
		/*=====Subject_type=====*/
		sql = "CREATE TABLE " + SUBJECT_TYPE_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumns.NAME + " TEXT);";
		db.execSQL(sql);
		for(String type : SUBJECT_TYPES){
			sql = "INSERT INTO " + SUBJECT_TYPE_TABLE_NAME + " (" + DBColumns.NAME + ") VALUES ('" + type + "');";
			db.execSQL(sql);
		}
		Log.i(TAG, sql);
		
		/*=====Teacher=====*/
		sql = "CREATE TABLE " + TEACHER_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumns.NAME + " TEXT);";
		db.execSQL(sql);
		Log.i(TAG, sql);
		
		/*=====Time=====*/
		sql = "CREATE TABLE " + TIME_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumns.START_HOUR + " INTEGER, "
				+ DBColumns.START_MINUTES + " INTEGER, "
				+ DBColumns.END_HOUR + " INTEGER, "
				+ DBColumns.END_MINUTES + " INTEGER);";
		Log.i(TAG, sql);
		db.execSQL(sql);
		
		/*=====Schedule=====*/
		sql = "CREATE TABLE " + SCHEDULE_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumns.SUBJECT_ID + " INTEGER, "
				+ DBColumns.SUBJECT_TYPE + " INTEGER, "
				+ DBColumns.TIME_ID + " INTEGER, "
				+ DBColumns.DAY + " INTEGER, "
				+ DBColumns.TEACHER_ID + " INTEGER, "
				+ DBColumns.WEEK + " INTEGER DEFAULT 0, "
				+ DBColumns.ROOM + " TEXT, "
				+ DBColumns.SUBGROUP + " INTEGER DEFAULT 0, "
				+ "FOREIGN KEY(" + DBColumns.SUBJECT_ID + ") REFERENCES " + SUBJECT_TABLE_NAME + "(" + BaseColumns._ID + "), "
				+ "FOREIGN KEY(" + DBColumns.SUBJECT_TYPE + ") REFERENCES " + SUBJECT_TYPE_TABLE_NAME + "(" + BaseColumns._ID + "), "
				+ "FOREIGN KEY(" + DBColumns.TIME_ID + ") REFERENCES " + TIME_TABLE_NAME + "(" + BaseColumns._ID + "), "
				+ "FOREIGN KEY(" + DBColumns.DAY + ") REFERENCES " + DAY_TABLE_NAME + "(" + BaseColumns._ID + "), "
				+ "FOREIGN KEY(" + DBColumns.TEACHER_ID + ") REFERENCES " + TEACHER_TABLE_NAME + "(" + BaseColumns._ID + "));";
		
		Log.i(TAG, sql);
		db.execSQL(sql);
		
		
		/*=====Note=====*/
		sql = "CREATE TABLE " + NOTE_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumns.SCHEDULE_ID + " INTEGER, "
				+ DBColumns.TEXT_NOTE + " TEXT, "
				+ DBColumns.DATE + " TEXT,"
				+ "FOREIGN KEY(" + DBColumns.SCHEDULE_ID + ") REFERENCES " + SCHEDULE_TABLE_NAME + "(" + BaseColumns._ID + "));";
		db.execSQL(sql);
		Log.i(TAG, sql);
		
		/*=====Schedule_view=====*/
		/*
CREATE VIEW schedule_view AS SELECT 
	schedule._id,
	subject.name as "subject",
	subject_type.name as "subject_type",
	time.start_hour,
	time.start_minutes,
	time.end_hour,
	time.end_minutes,
	day.name as "day",
	schedule.week,
	schedule.room,
	schedule.subgroup,
	teacher.name as "teacher"
FROM 
	schedule,
	subject,
	subject_type,
	time,
	teacher,
	day
WHERE 
subject._id = schedule.subject_id and 
subject_type._id = schedule.SUBJECT_TYPE and 
time._id = schedule.time_id and 
day._id = schedule.DAY and 
teacher._id = schedule.teacher_id; */
		
		sql = "CREATE VIEW IF NOT EXISTS " + SCHEDULE_VIEW_NAME + " AS SELECT "
				 + SCHEDULE_TABLE_NAME + "." + BaseColumns._ID + ", "
				 + SUBJECT_TABLE_NAME + "." + DBColumns.NAME + " as " + DBColumns.VIEW_SUBJECT + ", "
				 //+ SUBJECT_TYPE_TABLE_NAME + "." + DBColumns.NAME + " as " + DBColumns.VIEW_SUBJECT_TYPE + ", "
				 + TIME_TABLE_NAME + "." + DBColumns.START_HOUR + ", "
				 + TIME_TABLE_NAME + "." + DBColumns.START_MINUTES + ", "
				 + TIME_TABLE_NAME + "." + DBColumns.END_HOUR + ", "
				 + TIME_TABLE_NAME + "." + DBColumns.END_MINUTES + ", "
				 + SCHEDULE_TABLE_NAME +  "." + DBColumns.DAY + ", "
				 + SCHEDULE_TABLE_NAME + "." + DBColumns.WEEK + ", "
				 + SCHEDULE_TABLE_NAME + "." + DBColumns.ROOM + ", "
				 + SCHEDULE_TABLE_NAME + "." + DBColumns.SUBGROUP + ", "
				 + SCHEDULE_TABLE_NAME + "." + DBColumns.SUBJECT_TYPE + ", "
				 + TEACHER_TABLE_NAME + "." + DBColumns.NAME + " as " + DBColumns.VIEW_TEACHER
				 + " FROM " 
				 + SCHEDULE_TABLE_NAME + ", " 
				 + SUBJECT_TABLE_NAME + ", " 
				 //+ SUBJECT_TYPE_TABLE_NAME + ", " 
				 + TIME_TABLE_NAME + ", "
				 + TEACHER_TABLE_NAME// + ", "
				 //+ DAY_TABLE_NAME
				 + " WHERE "
				 + SUBJECT_TABLE_NAME + "." + BaseColumns._ID + " = " + SCHEDULE_TABLE_NAME + "." + DBColumns.SUBJECT_ID + " and "
			//	 + SUBJECT_TYPE_TABLE_NAME + "." + BaseColumns._ID + " = " + SCHEDULE_TABLE_NAME + "." + DBColumns.SUBJECT_TYPE + " and "
				 + TIME_TABLE_NAME + "." + BaseColumns._ID + " = " + SCHEDULE_TABLE_NAME + "." + DBColumns.TIME_ID + " and "
				// + DAY_TABLE_NAME + "." + BaseColumns._ID + " = " + SCHEDULE_TABLE_NAME + "." + DBColumns.DAY + " and "
				 + TEACHER_TABLE_NAME + "." + BaseColumns._ID + " = " + SCHEDULE_TABLE_NAME + "." + DBColumns.TEACHER_ID + ";";
		Log.i(TAG, sql);
		db.execSQL(sql);
		
		
		/*sql = "CREATE VIEW %s AS SELECT %s.%s, %s.%s as \"%s\", %s.%s as \"%s\", %s.%s, %s.%s, %s.%s as \"%s\"," +
				" %s.%s, %s.%s as \"%s\", %s.%s, %s.%s, %s.%s as \"%s\" " +
				"FROM %s, %s, %s, %s, %s" +
				"WHERE %s.%s = %s.%s and %s.%s = %s.%s and %s.%s = %s.%s and%s.%s = %s.%s and %s.%s = %s.%s;";
		sql = String.format(sql,SCHEDULE_VIEW_NAME, // AS SELECT
				SCHEDULE_TABLE_NAME, BaseColumns._ID, SUBJECT_TABLE_NAME,
				DBColumns.NAME, SUBJECT_TABLE_NAME,	SUBJECT_TYPE_TABLE_NAME, DBColumns.NAME,
				SUBJECT_TYPE_TABLE_NAME, TIME_TABLE_NAME, DBColumns.START_HOUR, TIME_TABLE_NAME,
				DBColumns.START_MINUTES, TIME_TABLE_NAME, DBColumns.END_HOUR, TIME_TABLE_NAME,
				DBColumns.END_MINUTES, DAY_TABLE_NAME, DBColumns.NAME, SCHEDULE_TABLE_NAME,
				DBColumns.WEEK,	SCHEDULE_TABLE_NAME, DBColumns.ROOM, //FROM =>
				SCHEDULE_TABLE_NAME,
				SUBJECT_TABLE_NAME,
				SUBJECT_TABLE_NAME,
				TIME_TABLE_NAME,
				TEACHER_TABLE_NAME,
				DAY_TABLE_NAME, // WHERE =>
				SUBJECT_TABLE_NAME, 		BaseColumns._ID, SCHEDULE_TABLE_NAME, DBColumns.SUBJECT_ID,
				SUBJECT_TYPE_TABLE_NAME, 	BaseColumns._ID, SCHEDULE_TABLE_NAME, DBColumns.SUBJECT_TYPE,
				TIME_TABLE_NAME, 			BaseColumns._ID, SCHEDULE_TABLE_NAME, DBColumns.TIME_ID,
				DAY_TABLE_NAME, 			BaseColumns._ID, SCHEDULE_TABLE_NAME, DBColumns.DAY,
				TEACHER_TABLE_NAME, 		BaseColumns._ID, SCHEDULE_TABLE_NAME, DBColumns.TEACHER_ID);*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= newVersion){
			return;
		}
		dropTables(db);
		db.execSQL("DROP VIEW IF EXISTS " + SCHEDULE_VIEW_NAME);
	}
	
	public void dropTables(SQLiteDatabase db){
		Log.i("DBAdapter", "db upgraded");
		db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE_NAME); 
		db.execSQL("DROP TABLE IF EXISTS " + TIME_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TEACHER_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SUBJECT_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SUBJECT_TYPE_TABLE_NAME);
	//	db.execSQL("DROP TABLE IF EXISTS " + DAY_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE_TABLE_NAME);
		onCreate(db);
	}
	
	private long getItemWithNameValue(String tableName, String columnName, String value){
		try {
			long t = System.currentTimeMillis();
			SQLiteDatabase db = this.getReadableDatabase();
			mTotalTime += (System.currentTimeMillis()-t);
			Log.d("Total time", ""+mTotalTime);
			Cursor cursor = db.query(
					tableName,
					new String[]{BaseColumns._ID, columnName},
					columnName + " = ? ",
					new String[] {value},
					null, null, null
					);
			cursor.moveToFirst();
			if (cursor.getCount() == 0) {
				cursor.close();
				return -1;
			} else {
				long result = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
				cursor.close();
				return result;
			}
		} catch (Exception e) {
			Log.d(TAG, e.getMessage() 
					+ "Cannot get item with column = " 
					+ columnName 
					+ " and value = " 
					+ value);
			return -1;
		} 
	}
	
	private long addSubjectItem(String subjectName){
		long itemId = getItemWithNameValue(SUBJECT_TABLE_NAME, DBColumns.NAME, subjectName);
		
		if(itemId < 0) { //item doesn't exist
			long t = System.currentTimeMillis();
			SQLiteDatabase db = this.getWritableDatabase();

			mTotalTime += (System.currentTimeMillis()-t);
			Log.d("Total time", ""+mTotalTime);
			ContentValues values = new ContentValues();
			values.put(DBColumns.NAME, subjectName);
			itemId = db.insert(SUBJECT_TABLE_NAME, null, values);
			db.close();
			return itemId;
		}
		else // item exists
			return itemId; 
	}
	
	private long addTeacherItem(String teacher){
		long itemId = getItemWithNameValue(TEACHER_TABLE_NAME, DBColumns.NAME, teacher);
		if(itemId < 0) { //item doesn't exist

			long t = System.currentTimeMillis();
			SQLiteDatabase db = this.getWritableDatabase();

			mTotalTime += (System.currentTimeMillis()-t);
			Log.d("Total time", ""+mTotalTime);
			ContentValues values = new ContentValues();
			values.put(DBColumns.NAME, teacher );
			itemId = db.insert(TEACHER_TABLE_NAME, null, values);
			db.close();
			return itemId;
		}
		else // item exists
			return itemId; 
	}
	
	private long addTimeItem(int startHour, int startMinutes, int endHour, int endMinutes){
		long resultId;


		long t = System.currentTimeMillis();
		SQLiteDatabase db = this.getWritableDatabase();

		mTotalTime += (System.currentTimeMillis()-t);
		Log.d("Total time", ""+mTotalTime);
		Cursor cursor = db.query(
			TIME_TABLE_NAME,
			new String[] { BaseColumns._ID, DBColumns.START_HOUR},
			DBColumns.START_HOUR + "=?",
			new String[] { String.valueOf(startHour)}, null, null,
			null
		);
		
		cursor.moveToFirst();		
		if (cursor.getCount() == 0) {
			//Log.i(TAG, "time doesn't exist");
			ContentValues values = new ContentValues();
			values.put(DBColumns.START_HOUR, startHour);
			values.put(DBColumns.START_MINUTES, startMinutes);
			values.put(DBColumns.END_HOUR, endHour);
			values.put(DBColumns.END_MINUTES, endMinutes);
			resultId = db.insert(TIME_TABLE_NAME, null, values);
			db.close();
			
		}
		else													
			resultId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
		
		cursor.close();
		
		return resultId;
	}
	
	public long updateNote(long scheduleId, String text, GregorianCalendar date) {

		long t = System.currentTimeMillis();
		SQLiteDatabase db = this.getWritableDatabase();

		mTotalTime += (System.currentTimeMillis()-t);
		Log.d("Total time", ""+mTotalTime);
		String qDate = (new SimpleDateFormat(DATE_FORMAT)).format(date);
		
		Cursor cursor = db.query(
				NOTE_TABLE_NAME,
				null,
				DBColumns.SCHEDULE_ID + " =? AND" + DBColumns.DATE + " =? ",
				new String[] {String.valueOf(scheduleId), qDate},
				null, null, null
			);
		
		if(cursor.getCount() != 0) {
			ContentValues values = new ContentValues();
			values.put(DBColumns.TEXT_NOTE, text);
			return db.update(
					NOTE_TABLE_NAME,
					values,
					DBColumns.SCHEDULE_ID + " =? AND" + DBColumns.DATE + " =? ",
					new String[] {String.valueOf(scheduleId), qDate}
				);
		} else {
			db.close();
			return addNoteItem(scheduleId, text, date);
		}		
	}
	
	private long addNoteItem(long scheduleId, String text, GregorianCalendar date) {
		String qDate = (new SimpleDateFormat(DATE_FORMAT)).format(date);

		long t = System.currentTimeMillis();
		SQLiteDatabase db = this.getWritableDatabase();

		mTotalTime += (System.currentTimeMillis()-t);
		Log.d("Total time", ""+mTotalTime);
		ContentValues values = new ContentValues();
		values.put(DBColumns.SCHEDULE_ID, scheduleId);
		values.put(DBColumns.TEXT_NOTE, text);
		values.put(DBColumns.DATE, qDate);
		long noteId = db.insert(NOTE_TABLE_NAME, null, values);
		db.close();
		return noteId;
	}
	
	public String getNote(long scheduleId, GregorianCalendar date) {
		long t = System.currentTimeMillis();
		SQLiteDatabase db = this.getReadableDatabase();
		mTotalTime += (System.currentTimeMillis()-t);
		Log.d("Total time", ""+mTotalTime);
		String qDate = date.get(
							GregorianCalendar.DAY_OF_MONTH) + "."
							+ date.get(GregorianCalendar.MONTH) + "."
							+ date.get(GregorianCalendar.YEAR
						);
		Cursor cursor = db.query(
							NOTE_TABLE_NAME,
							new String[] { DBColumns.TEXT_NOTE },
							DBColumns.SCHEDULE_ID + " =? AND " + DBColumns.DATE + " =? " ,
							new String[] { String.valueOf(scheduleId), qDate },
							null, null, null
						);
		db.close();
		if(cursor.isNull(cursor.getColumnIndex(DBColumns.TEXT_NOTE)))
			return "";
		else
			return cursor.getString((cursor.getColumnIndex(DBColumns.TEXT_NOTE)));
	}
	
	public void addScheduleItem(String subjectName, int subjectType, int startHour, int startMinutes, int endHour, int endMinutes, int dayId, int week, String room, String teacherName, int subgroup) {
		ContentValues values = new ContentValues();
		values.put(DBColumns.SUBJECT_ID, addSubjectItem(subjectName));
		values.put(DBColumns.SUBJECT_TYPE, subjectType);
		values.put(DBColumns.TIME_ID, addTimeItem(startHour, startMinutes, endHour, endMinutes));
		values.put(DBColumns.DAY, dayId);
		values.put(DBColumns.WEEK, week);
		values.put(DBColumns.ROOM, room);
		values.put(DBColumns.TEACHER_ID, addTeacherItem(teacherName));
		values.put(DBColumns.SUBGROUP, subgroup);
		long t = System.currentTimeMillis();
		SQLiteDatabase db = this.getWritableDatabase();
		mTotalTime += (System.currentTimeMillis()-t);
		Log.d("Total time", ""+mTotalTime);
		db.insert(SCHEDULE_TABLE_NAME, null, values);
		db.close();
	}
	
	public Cursor getDay(int dayId) {
		long t = System.currentTimeMillis();
		SQLiteDatabase db = getReadableDatabase();
		mTotalTime += (System.currentTimeMillis()-t);
		Log.d("Total time", ""+mTotalTime);
		Cursor cursor = db.query(DAY_TABLE_NAME, new String[]{DBColumns.NAME}, DBColumns.DAY + " = " +(""+ dayId), null, null, null, null, null);
		String dayName = cursor.getString(cursor.getColumnIndex(DBColumns.NAME));
		return db.query(SCHEDULE_VIEW_NAME, null, DBColumns.VIEW_DAY + " = " + dayName, null, null, null, null, DBColumns.START_HOUR);
	}
}
