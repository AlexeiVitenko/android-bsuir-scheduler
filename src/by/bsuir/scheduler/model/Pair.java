package by.bsuir.scheduler.model;

import java.util.GregorianCalendar;

/**
 * 
 * @author alexei
 * //TODO добавить геттеров
 */
public class Pair{
	private int mPairIndex;
	private int mWeek;
	private String mLesson;
	private String mTeacher;
	private String mRoom;
//	private String mStringType;
	private int mSubGroup;
	private int mType;
	private int mBeginningHours = -1;
	private int mBeginningMinutes = -1;
	private int mEndingHours = -1;
	private int mEndingMinutes = -1;
	private Day mDay;
	private String mNote;
	private GregorianCalendar mDate;
	private DBAdapter mAdapter;
	private int mScheduleId;
	protected Pair(Day container, int week, int subGroup, String lesson, int type/*, String sType*/, String room, String teacher, int times[],int index, int schedule){
		mDay = container;
		mSubGroup = subGroup;
		mBeginningHours = times[0];
		mBeginningMinutes = times[1];
		mEndingHours = times[2];
		mEndingMinutes = times[3];	
//		mStringType = sType;
		mWeek = week;
		mType = type;

		mLesson = lesson;
		mTeacher = teacher;
		mRoom = room;
		mNote = "";
		mPairIndex = index;
		mScheduleId = schedule;
	}
	
	protected Pair(DBAdapter adapter, GregorianCalendar date, int week, int subGroup, String lesson, int type/*, String sType*/, String room, String teacher, int times[], int index, int schedule){
		mDate = date;
		mAdapter = adapter;
		mSubGroup = subGroup;
		mBeginningHours = times[0];
		mBeginningMinutes = times[1];
		mEndingHours = times[2];
		mEndingMinutes = times[3];	
//		mStringType = sType;
		mWeek = week;
		mType = type;

		mLesson = lesson;
		mTeacher = teacher;
		mRoom = room;
		mPairIndex = index;
		mScheduleId = schedule;
		

		mNote = adapter.getNote(date, schedule);
	}
	
	public String getNote(){
		return mNote;
	}
	
	public void setNote(String note){
		mNote = note;
		mAdapter.changeNote(mDate,mScheduleId, note);
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	
	public int getWeek(){
		return mWeek;
	}
	
	public int getSubGroup() {
		return mSubGroup;
	}
	
	public String getLesson() {
		return mLesson;
	}

	public int getType() {
		return mType;
	}
	/*
	public String getStringType() {
		return mStringType;
	}*/
	
	public String getRoom(){
		return mRoom;
	}
	
	public String getTeacher() {
		return mTeacher;
	}
	
	public int[] getTime() {
		return new int[]{mBeginningHours,mBeginningMinutes,mEndingHours,mEndingMinutes};
	}
	
	public int getId(){
		return mScheduleId;
	}
}
