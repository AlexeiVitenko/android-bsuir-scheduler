package by.bsuir.scheduler.model;

import java.io.Closeable;
import java.io.IOException;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
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
	private DBHelper dbHelper;
	
	protected DBAdapter(Context context){
		mContext = context;
		dbHelper = new DBHelper(context);
	}
	
	/**
	 * Возвращает структуру типа Day.
	 * @param day
	 */
	public Day getDay(GregorianCalendar day){
		return new Day(day, this);
	}
	
	/**
	 * Проверяет, является ли день учебным. Необходимо для календаря месяца и подгрузки дней.
	 * @param day
	 * @return true - если да.
	 */
	public boolean isWorkDay(GregorianCalendar day){
		Cursor cursor = dbHelper.getDay(String.valueOf(day.get(GregorianCalendar.DAY_OF_WEEK)));
		if(cursor.getCount() != 0)
			//TODO добавить проверку на номер подгруппы и номер недели
			return true;
		else
			return false;
		//return day.get(GregorianCalendar.DAY_OF_WEEK)!=GregorianCalendar.SUNDAY;
	}
	
	public Pair getPair(GregorianCalendar date, int scheduleId){
		Cursor cursor = dbHelper.getDay(String.valueOf(date.get(GregorianCalendar.DAY_OF_WEEK)));
		//TODO определить
		return (new Day(date, this).getPair(scheduleId));
	}
	
	public void changeNote(GregorianCalendar day, int schId, String note){
		//TODO реализовать
	}
	
	@Override
	/**
	 * Наш адаптер также будет принимать пары от парсера. В этом ему поможет наш метод
	 */
	public void push(Lesson lesson) {
		//TODO Определить
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
	}

}
