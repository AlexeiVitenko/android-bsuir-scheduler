package by.bsuir.scheduler.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;


public class Day {
	private DBAdapter mDbAdapter;
	private GregorianCalendar mDate;
	private List<Pair> mPairs;

	public Pair getPair(int index){
		return mPairs.get(index);
	}
	
	public List<Pair> getPairs() {
		return mPairs;
	}
	
	protected Day(GregorianCalendar day){
		mDate = day;
		mDbAdapter = DBAdapter.getInstance();
		mPairs = new ArrayList<Pair>();
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
		return -1;
	}
	
	public Iterator<Pair> iterator() {
		return mPairs.iterator();
	}
	
	protected void changeNote(int scheduleId, String note){
		mDbAdapter.changeNote(mDate,scheduleId, note);
	}
	
	private void generatePairs(){
		Pair lesson1 = new Pair(this, 0, 0, "Ололлогия", 1,"лк", "108-4", "Иванов", new int[]{8,00,9,35}, null, 1);
		Pair lesson2 = new Pair(this, 0, 1, "Ололлогия", 3,"лр", "210-4", "Иванов", new int[]{9,45,11,20}, null, 2);
		Pair lesson3 = new Pair(this, 0, 0, "Физра", -1,"", "", "", new int[] {11,40,13,25}, null, 3);
		mPairs.add(lesson1);
		mPairs.add(lesson2);
		mPairs.add(lesson3);
	}
}
