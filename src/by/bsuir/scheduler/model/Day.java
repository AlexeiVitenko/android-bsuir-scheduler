package by.bsuir.scheduler.model;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import android.database.Cursor;
import android.provider.BaseColumns;


public class Day {
	private DBAdapter mDbAdapter;
	private GregorianCalendar mDate;
	private List<Pair> mPairs;
	private int mWeek;
	public Pair getPair(int index){
		return mPairs.get(index);
	}
	
	public List<Pair> getPairs() {
		return mPairs;
	}
	
	protected Day(GregorianCalendar day, Cursor data, DBAdapter dbAdapter, int week){
		mDate = day;
		mDbAdapter = dbAdapter;
		mPairs = new ArrayList<Pair>();
		generatePairs(data);
	}
	
	/**
	 * Необходмо для отметки текущей пары, проученных, оставшихся.
	 * @param time - время, в которое совершался запрос
	 * @return
	 */
	public int getCurrentLessonIndex(GregorianCalendar time){
		//TODO Определить
		return -1;
	}
	public int getCurrentLessonIndex(Pair pair){
		return mPairs.indexOf(pair);
	}
	
	public int getWeek(){
		return mWeek;
	}
	
	public Iterator<Pair> iterator() {
		return mPairs.iterator();
	}
	
	protected void changeNote(int scheduleId, String note){
		mDbAdapter.changeNote(mDate,scheduleId, note);
	}
	
	protected GregorianCalendar getDate(){
		return mDate;
	}
	
	private void generatePairs(Cursor data){
		if (data.getCount()<1) {
			return;
		}
		int ID = data.getColumnIndex(BaseColumns._ID);
		int DAY = data.getColumnIndex(DBColumns.DAY);
		int WEEK = data.getColumnIndex(DBColumns.WEEK);
		int SUBJECT = data.getColumnIndex(DBColumns.VIEW_SUBJECT);
		int SUBJECT_TYPE = data.getColumnIndex(DBColumns.SUBJECT_TYPE);
		int ROOM = data.getColumnIndex(DBColumns.ROOM);
		int SUBGROUP = data.getColumnIndex(DBColumns.SUBGROUP);
		int TEACHER = data.getColumnIndex(DBColumns.VIEW_TEACHER);
		int START_HOUR = data.getColumnIndex(DBColumns.START_HOUR);
		int START_MINUTES = data.getColumnIndex(DBColumns.START_MINUTES);
		int END_HOUR = data.getColumnIndex(DBColumns.END_HOUR);
		int END_MINUTES = data.getColumnIndex(DBColumns.END_MINUTES);
		data.moveToFirst();
		int i = 0;
		do {
			mPairs.add(new Pair(this, 
					data.getInt(WEEK),
					data.getInt(SUBGROUP),
					data.getString(SUBJECT),
					data.getInt(SUBJECT_TYPE), 
					data.getString(ROOM),
					data.getString(TEACHER),
					new int[]{
					data.getInt(START_HOUR),
					data.getInt(START_MINUTES),
					data.getInt(END_HOUR),
					data.getInt(END_MINUTES)
				}, i,data.getInt(ID)));
			i++;
		} while (data.moveToNext());
		data.close();
	}
}
