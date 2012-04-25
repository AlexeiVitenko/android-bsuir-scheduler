package by.bsuir.scheduler.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "scheduledb";
	private static final int DATABASE_VERSION = 1;
	private static final String SCHEDULE_TABLE_NAME = "schedule";
	private static final String NOTE_TABLE_NAME = "note";
	private static final String SUBJECT_TABLE_NAME = "subject";
	private static final String TIME_TABLE_NAME = "time";
	private static final String SUBJECT_TYPE_TABLE_NAME = "subject_type";
	private static final String DAY_TABLE_NAME = "day";
	private static final String TEACHER_TABLE_NAME = "teacher";
	private static final String[] DAYS = {"понедельник", "вторник", "среда", "четверг", "пятница", "суббота"};
	private static final String[] SUBJECT_TYPES = {"", "лекция", "практическое занятие", "лабораторная работа", "курсовое проектирование"};
	private static final String SCHEDULE_VIEW_NAME = "schedule_view";
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("PRAGMA foreign_keys=ON;");
		
		/*=====Subject=====*/
		String sql = "CREATE TABLE " + SUBJECT_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumns.NAME + " TEXT);";
		db.execSQL(sql);
		
		/*=====Day=====*/
		sql = "CREATE TABLE " + DAY_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumns.NAME + " TEXT);";
		db.execSQL(sql);
		for(String day : DAYS){
			sql = "INSERT INTO " + DAY_TABLE_NAME + " (" + DBColumns.NAME+ ") VALUES ('" + day + "');";
			db.execSQL(sql);
		}
		
		/*=====Subject_type=====*/
		sql = "CREATE TABLE " + SUBJECT_TYPE_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumns.NAME + " TEXT);";
		db.execSQL(sql);
		for(String type : SUBJECT_TYPES){
			sql = "INSERT INTO " + SUBJECT_TYPE_TABLE_NAME + " (" + DBColumns.NAME+ ") VALUES ('" + type + "');";
			db.execSQL(sql);
		}

		
		/*=====Teacher=====*/
		sql = "CREATE TABLE " + TEACHER_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumns.NAME + " TEXT);";
		db.execSQL(sql);
		
		/*=====Time=====*/
		sql = "CREATE TABLE " + TIME_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumns.START_HOUR + " INTEGER, "
				+ DBColumns.START_MINUTES + " INTEGER, "
				+ DBColumns.END_HOUR + " INTEGER, "
				+ DBColumns.END_MINUTES + " INTEGER);";
		db.execSQL(sql);
		
		/*=====Schedule=====*/
		sql = "CREATE TABLE " + SCHEDULE_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "FOREIGN KEY(" + DBColumns.SUBJECT_ID + ") REFERENCES " + SUBJECT_TABLE_NAME + "(" + BaseColumns._ID + "), "
				+ "FOREIGN KEY(" + DBColumns.SUBJECT_TYPE_ID + ") REFERENCES " + SUBJECT_TYPE_TABLE_NAME + "(" + BaseColumns._ID + "), "
				+ "FOREIGN KEY(" + DBColumns.TIME_ID + ") REFERENCES " + TIME_TABLE_NAME + "(" + BaseColumns._ID + "), "
				+ "FOREIGN KEY(" + DBColumns.DAY_ID + ") REFERENCES " + DAY_TABLE_NAME + "(" + BaseColumns._ID + "), "
				+ DBColumns.WEEK + " INTEGER DEFAULT 0, "
				+ DBColumns.ROOM + " TEXT, "
				+ "FOREIGN KEY(" + DBColumns.TEACHER_ID + ") REFERENCES " + TEACHER_TABLE_NAME + "(" + BaseColumns._ID + "), "
				+ DBColumns.SUBGROUP + " INTEGER DEFAULT 0);";
		db.execSQL(sql);
		
		/*=====Note=====*/
		sql = "CREATE TABLE " + NOTE_TABLE_NAME + 
				" (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "FOREIGN KEY(" + DBColumns.SCHEDULE_ID + ") REFERENCES " + SCHEDULE_TABLE_NAME + "(" + BaseColumns._ID + "), "
				+ DBColumns.TEXT + " TEXT, "
				+ DBColumns.DATE + " TEXT;";
		db.execSQL(sql);
		
		/*=====Schedule_view=====*/
		/*
CREATE VIEW schedule_view
AS SELECT schedule._id,  subject.name as "subject", subject_type.name as "subject_type",
time.start_hour, time.start_minutes, time.end_hour,
time.end_minutes, day.name as "day", schedule.weekd, schedule.room,
teacher.name as "teacher"
FROM schedule, subject, subject_type, time, teacher, day
WHERE subject._id = schedule.subject_id and subject_type._id = schedule.subject_type_id and time._id = schedule.time_id
 and day._id = schedule.day_id and teacher._id = schedule.teacher_id; */
		sql = "CREATE VIEW %s AS SELECT %s.%s, %s.%s as \"%s\", %s.%s as \"%s\", %s.%s, %s.%s, %s.%s as \"%s\"," +
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
				SUBJECT_TYPE_TABLE_NAME, 	BaseColumns._ID, SCHEDULE_TABLE_NAME, DBColumns.SUBJECT_TYPE_ID,
				TIME_TABLE_NAME, 			BaseColumns._ID, SCHEDULE_TABLE_NAME, DBColumns.TIME_ID,
				DAY_TABLE_NAME, 			BaseColumns._ID, SCHEDULE_TABLE_NAME, DBColumns.DAY_ID,
				TEACHER_TABLE_NAME, 		BaseColumns._ID, SCHEDULE_TABLE_NAME, DBColumns.TEACHER_ID);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= newVersion){
			return;
		}
		Log.i("DBAdapter", "db upgraded");
		db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE_NAME); 
		db.execSQL("DROP TABLE IF EXISTS " + TIME_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TEACHER_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SUBJECT_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SUBJECT_TYPE_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DAY_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE_TABLE_NAME);
		onCreate(db);
	}
	
	private long getItemId(String tableName, String columnName, String value){
		Cursor cursor = getReadableDatabase().query(
				tableName,
				new String[] {BaseColumns._ID, columnName},
				columnName + " =? ",
				new String[] {value},
				null, null, null
				);
		if (cursor != null)
			cursor.moveToFirst();
		else
			return -1;
		return cursor.getInt(0);
	}
	
	private long addSubjectItem(String subjectName){
		long itemId = getItemId(SUBJECT_TABLE_NAME, DBColumns.NAME, subjectName);
		if(itemId < 0) { //item doesn't exist
			SQLiteDatabase db = this.getWritableDatabase();
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
		long itemId = getItemId(TEACHER_TABLE_NAME, DBColumns.NAME, teacher);
		if(itemId < 0) { //item doesn't exist
			SQLiteDatabase db = this.getWritableDatabase();
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
		Cursor cursor = getReadableDatabase()
				.query(TIME_TABLE_NAME,
						new String[] { BaseColumns._ID, },
						DBColumns.START_HOUR + "=?  AND "
								+ DBColumns.START_MINUTES + "=? AND "
								+ DBColumns.END_HOUR + "=? AND "
								+ DBColumns.END_MINUTES + "=?",
						new String[] { String.valueOf(startHour),
								String.valueOf(startMinutes),
								String.valueOf(endHour),
								String.valueOf(endMinutes) }, null, null,
						null);
		if (cursor != null) 									// item exists
			resultId = cursor.getLong(0);
		else { 													//item doesn't exist
			SQLiteDatabase db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DBColumns.START_HOUR, startHour);
			values.put(DBColumns.START_MINUTES, startMinutes);
			values.put(DBColumns.END_HOUR, endHour);
			values.put(DBColumns.END_MINUTES, endMinutes);
			resultId = db.insert(TIME_TABLE_NAME, null, values);
			db.close();
		}
		cursor.close();
		
		return resultId;
	}
	
	private long addNoteItem(long scheduleId, String text, String date) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBColumns.SCHEDULE_ID, scheduleId);
		values.put(DBColumns.TEXT, text);
		values.put(DBColumns.DATE, date);
		long noteId = db.insert(NOTE_TABLE_NAME, null, values);
		db.close();
		return noteId;
	}
	
	public void addScheduleItem(String subjectName, String subjectType, int startHour, int startMinutes, int endHour, int endMinutes, String dayName, int week, String room, String teacherName) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBColumns.SUBJECT_ID, addSubjectItem(subjectName));
		values.put(DBColumns.SUBJECT_TYPE_ID, getItemId(SUBJECT_TYPE_TABLE_NAME, DBColumns.NAME, subjectType));
		values.put(DBColumns.TIME_ID, addTimeItem(startHour, startMinutes, endHour, endMinutes));
		values.put(DBColumns.DAY_ID, getItemId(DAY_TABLE_NAME, DBColumns.NAME, dayName)); // �������� �������� �������� �� "��" ��� "�����������"
		values.put(DBColumns.WEEK, week);
		values.put(DBColumns.ROOM, room);
		values.put(DBColumns.TEACHER_ID, teacherName);
		db.insert(SCHEDULE_TABLE_NAME, null, values);
		db.close();
	}
	
	/*public Cursor getDay(long day_id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(SCHEDULE_TABLE_NAME, 
				new String[] {DBColumns.SUBJECT_ID, DBColumns.TIME_ID, DBColumns.SUBJECT_TYPE_ID}, selection, selectionArgs, groupBy, having, orderBy)
	}*/
}
