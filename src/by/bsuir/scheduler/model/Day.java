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
	private Cursor mData;
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
		mData = data;
		generatePairs();
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
	
	private void generatePairs(){
		if (mData.getCount()<1) {
			return;
		}
		int DAY = mData.getColumnIndex(DBColumns.DAY);
		int WEEK = mData.getColumnIndex(DBColumns.WEEK);
		int SUBJECT = mData.getColumnIndex(DBColumns.VIEW_SUBJECT);
		int SUBJECT_TYPE = mData.getColumnIndex(DBColumns.SUBJECT_TYPE);
		int ROOM = mData.getColumnIndex(DBColumns.ROOM);
		int SUBGROUP = mData.getColumnIndex(DBColumns.SUBGROUP);
		int TEACHER = mData.getColumnIndex(DBColumns.VIEW_TEACHER);
		int START_HOUR = mData.getColumnIndex(DBColumns.START_HOUR);
		int START_MINUTES = mData.getColumnIndex(DBColumns.START_MINUTES);
		int END_HOUR = mData.getColumnIndex(DBColumns.END_HOUR);
		int END_MINUTES = mData.getColumnIndex(DBColumns.END_MINUTES);
		mData.moveToFirst();
		int i = 0;
		do {
			mPairs.add(new Pair(this, 
					mData.getInt(WEEK),
					mData.getInt(SUBGROUP),
					mData.getString(SUBJECT),
					mData.getInt(SUBJECT_TYPE), 
					mData.getString(ROOM),
					mData.getString(TEACHER),
					new int[]{
					mData.getInt(START_HOUR),
					mData.getInt(START_MINUTES),
					mData.getInt(END_HOUR),
					mData.getInt(END_MINUTES)
				}, i));
			i++;
		} while (mData.moveToNext());
	}
	
	@Override
	protected void finalize() throws Throwable {
		mData.close();
		super.finalize();
	}
}
