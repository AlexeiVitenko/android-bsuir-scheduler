package by.bsuir.scheduler.model;

import java.io.Closeable;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;
import by.bsuir.scheduler.DayPagerAdapter;
import by.bsuir.scheduler.parser.Lesson;
import by.bsuir.scheduler.parser.Pushable;

/**
 * 
 * @author Alexei
 *
 */
public class DBAdapter implements Pushable, Closeable{
	protected static DBAdapter mInstance;
	public static DBAdapter getInstance(Context context){
		if (mInstance == null) {
			mInstance = new DBAdapter(context);
		}
		return mInstance;
	}
	
	private Context mContext;
	private DBHelper mDBHelper;
	
	protected DBAdapter(Context context){
		mContext = context;
		mDBHelper = new DBHelper(context);
	}
	
	/**
	 * Возвращает структуру типа Day.
	 * @param day
	 */
	public Day getDay(GregorianCalendar day){
	//	Cursor cursor = mDBHelper.getDay(String.valueOf(day.get(GregorianCalendar.DAY_OF_WEEK)));
		return new Day(day, this);
	}
	
	/**
	 * Проверяет, является ли день учебным. Необходимо для календаря месяца и подгрузки дней.
	 * @param day
	 * @return true - если да.
	 */
	public boolean isWorkDay(GregorianCalendar day){
	//	Cursor cursor = mDBHelper.getDay(String.valueOf(day.get(GregorianCalendar.DAY_OF_WEEK)));
	//	if(cursor.getCount() != 0)
		if(day.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY)
			//TODO добавить проверку на номер подгруппы и номер недели
			return true;
		else
			return false;
		//return day.get(GregorianCalendar.DAY_OF_WEEK)!=GregorianCalendar.SUNDAY;
	}
	
	public Pair getPair(GregorianCalendar date) { // убрал параметр int scheduleId, т.к. получаю его из курсора
		Cursor cursor = mDBHelper.getDay(String.valueOf(date.get(GregorianCalendar.DAY_OF_WEEK)));
		//TODO добавить проверку на номер подгруппы и номер недели
		int[] times = new int[] {
				cursor.getInt(cursor.getColumnIndex(DBColumns.START_HOUR)),
				cursor.getInt(cursor.getColumnIndex(DBColumns.START_MINUTES)),
				cursor.getInt(cursor.getColumnIndex(DBColumns.END_HOUR)),
				cursor.getInt(cursor.getColumnIndex(DBColumns.END_MINUTES))
		};
		int week = cursor.getInt(cursor.getColumnIndex(DBColumns.WEEK));
		int subGroup = cursor.getInt(cursor.getColumnIndex(DBColumns.SUBGROUP));
		String lesson = cursor.getString(cursor.getColumnIndex(DBColumns.VIEW_SUBJECT));
		int type = cursor.getInt(cursor.getColumnIndex(DBColumns.VIEW_SUBJECT_TYPE));
		String sType = DayPagerAdapter.daysOfWeek[type];
		String room = cursor.getString(cursor.getColumnIndex(DBColumns.ROOM));
		String teacher = cursor.getString(cursor.getColumnIndex(DBColumns.VIEW_TEACHER));
		int schedule = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
		String note = mDBHelper.getNote(schedule, date);
		Pair pair = new Pair(new Day(date, this), week, subGroup, lesson, type, sType, room, teacher, times, note, schedule); // не уверен по поводу правильности параметра container
		return pair; //(new Day(date, this).getPair(scheduleId));
	}
	
	public void changeNote(GregorianCalendar day, int scheduleId, String note){
		mDBHelper.updateNote(scheduleId, note, day);
	}
	
	@Override
	/**
	 * Наш адаптер также будет принимать пары от парсера. В этом ему поможет наш метод
	 */
	public void push(Lesson lesson) { // для реализации метода необходимо наличие get/set у экземпляров класса Lesson
		Log.i("push", lesson.getLesson());
		mDBHelper.addScheduleItem(lesson.getLesson(), DBHelper.SUBJECT_TYPES[lesson.getType()], lesson.getBeginningHours(), lesson.getBeginningMinutes(), lesson.getEndingHours(), lesson.getEndingMinutes(),lesson.getDay() , lesson.getWeek(), lesson.getRoom(), lesson.getTeacher(), lesson.getSubGroup());
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		mDBHelper.close();
	}

}
