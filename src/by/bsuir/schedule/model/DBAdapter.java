package by.bsuir.schedule.model;

import java.io.Closeable;
import java.io.IOException;
import java.util.GregorianCalendar;

import by.bsuir.schedule.parser.Lesson;
import by.bsuir.schedule.parser.Pushable;

/**
 * 
 * @author Alexei
 *
 */
public class DBAdapter implements Pushable, Closeable{
	protected static DBAdapter mInstance;
	public static DBAdapter getInstance(){
		if (mInstance == null) {
			mInstance = new DBAdapter();
		}
		return mInstance;
	}
	
	protected DBAdapter(){
		
	}
	
	/**
	 * Возвращает структуру типа Day.
	 * @param day
	 */
	public Day getDay(GregorianCalendar day){
		return new Day(day);
	}
	
	/**
	 * Проверяет, является ли день учебным. Необходимо для календаря месяца и подгрузки дней.
	 * @param day
	 * @return true - если да.
	 */
	public boolean isWorkDay(GregorianCalendar day){
		return day.get(GregorianCalendar.DAY_OF_WEEK)!=GregorianCalendar.SUNDAY;
	}
	
	public Pair getPair(GregorianCalendar date, int scheduleId){
		//TODO определить
		return (new Day(date).getPair(scheduleId));
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
