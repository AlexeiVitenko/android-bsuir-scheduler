package by.bsuir.scheduler.model;

import java.io.Closeable;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;
import by.bsuir.scheduler.R;
import by.bsuir.scheduler.parser.Lesson;
import by.bsuir.scheduler.parser.Parser;
import by.bsuir.scheduler.parser.ParserListiner;
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
	//FIXEME доделать нормально
	private GregorianCalendar septFirst = new GregorianCalendar(2011, 9, 1);
	private Context mContext;
	private DBHelper mDBHelper;
	private String[] daysOfWeek;
	
	protected DBAdapter(Context context){
		mContext = context;
		mDBHelper = new DBHelper(context);
		daysOfWeek = mContext.getResources().getStringArray(
				R.array.days_of_week);
	}
	
	/**
	 * Возвращает структуру типа Day.
	 * @param day
	 */
	public Day getDay(GregorianCalendar day){
		int week = getWeekNumber(day);
		Cursor cursor = getDay(day.get(GregorianCalendar.DAY_OF_WEEK), week);
		return new Day(day, cursor, this, week);
	}
	
	/**
	 * Проверяет, является ли день учебным. Необходимо для календаря месяца и подгрузки дней.
	 * @param day
	 * @return true - если да.
	 */
	public boolean isWorkDay(GregorianCalendar day){
		Cursor cursor = getDay(day.get(GregorianCalendar.DAY_OF_WEEK),getWeekNumber(day));
		return cursor.getCount()>0;
	}
	
	private int getWeekNumber(GregorianCalendar day){
		int weeks = 0;
		if (day.get(Calendar.YEAR)>septFirst.get(Calendar.YEAR)) {
			if (day.getMinimalDaysInFirstWeek()!=7) {
				weeks--;
			}
			weeks += (new GregorianCalendar(2011, 11, 31).get(Calendar.WEEK_OF_YEAR)-septFirst.get(Calendar.WEEK_OF_YEAR)) +1;
			weeks += day.get(Calendar.WEEK_OF_YEAR);
		}else{
			weeks = day.get(Calendar.WEEK_OF_YEAR) - septFirst.get(Calendar.WEEK_OF_YEAR) + 1;
		}
		return weeks%4+1;
	}
	
	public Pair getPair(GregorianCalendar date)  {
		return null;
	}
	
	public Cursor getDay(int dayOfWeek,int week) {
		return mDBHelper.getReadableDatabase().query(DBHelper.SCHEDULE_VIEW_NAME, new String[]{
				BaseColumns._ID,
				DBColumns.DAY,
				DBColumns.WEEK,
				DBColumns.VIEW_SUBJECT,
				DBColumns.SUBJECT_TYPE,
				DBColumns.ROOM,
				DBColumns.SUBGROUP,
				DBColumns.VIEW_TEACHER,
				DBColumns.START_HOUR,
				DBColumns.START_MINUTES,
				DBColumns.END_HOUR,
				DBColumns.END_MINUTES
		}
		, DBColumns.DAY + " = ? AND "+DBColumns.WEEK + " IN (?,0)" , new String[]{
				""+dayOfWeek,
				""+week
		}, null, null, DBColumns.START_HOUR);
	} 
	
	public void changeNote(GregorianCalendar day, int scheduleId, String note){
		mDBHelper.updateNote(scheduleId, note, day);
	}
	
	public void refreshSchedule(String group, int subGroup, ParserListiner listiner){
		if (group.equals("-1")) {
			Toast.makeText(mContext, "Введите группу в настройках.", Toast.LENGTH_SHORT).show();
			return;
		}
		mDBHelper.dropTables(mDBHelper.getWritableDatabase());
		long startTime = System.currentTimeMillis();
		Parser p = new Parser(group, subGroup, this, listiner);
		p.parseSchedule();
		Log.d("Parse time", ""+(System.currentTimeMillis()-startTime));
	}
	
	@Override
	/**
	 * Наш адаптер также будет принимать пары от парсера. В этом ему поможет наш метод
	 */
	public void push(Lesson lesson) { // для реализации метода необходимо наличие get/set у экземпляров класса Lesson
		mDBHelper.addScheduleItem(lesson.getLesson(), lesson.getType(), lesson.getBeginningHours(), lesson.getBeginningMinutes(), lesson.getEndingHours(), lesson.getEndingMinutes(),lesson.getDay() , lesson.getWeek(), lesson.getRoom(), lesson.getTeacher(), lesson.getSubGroup());}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		mDBHelper.close();
	}
}
