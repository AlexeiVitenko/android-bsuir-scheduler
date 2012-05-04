package by.bsuir.scheduler.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 
 * @author alexei
 * //TODO добавить геттеров
 */
public class Pair{
	public static class PairStatus{
		
		PairStatus(int status){
			this.status = status;
		}
		
		PairStatus(int status, int progres){
			this(status);
			this.progress = progres;
		}
		
		int status;
		int progress;
	}
	public static final int PAIR_STATUS_PAST = 1;
	public static final int PAIR_STATUS_FUTURE = 2;
	public static final int PAIR_STATUS_CURRENT = 4;
	public static final int PAIR_STATUS_CURRENT_DAY_PAST = 8;
	public static final int PAIR_STATUS_CURRENT_DAY_FUTURE = 16;
	
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
		mDate = container.getDate();
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
	
	public PairStatus getStatus() {
		GregorianCalendar time = new GregorianCalendar(Locale.getDefault());
		time.setTimeInMillis(System.currentTimeMillis());
		if (mDate.get(Calendar.YEAR)<time.get(Calendar.YEAR)) {
			return new PairStatus(PAIR_STATUS_PAST);
		} else {
			if (mDate.get(Calendar.YEAR)>time.get(Calendar.YEAR)) {
				return new PairStatus(PAIR_STATUS_FUTURE);
			} else {
				if (mDate.get(Calendar.DAY_OF_YEAR)<time.get(Calendar.DAY_OF_YEAR)) {
					return new PairStatus(PAIR_STATUS_PAST);
				} else {
					if (mDate.get(Calendar.DAY_OF_YEAR)>time.get(Calendar.DAY_OF_YEAR)) {
						return new PairStatus(PAIR_STATUS_FUTURE);
					} else {
						int hours = time.get(Calendar.HOUR_OF_DAY);
						int minutes = time.get(Calendar.MINUTE);
						int start = mBeginningHours*60+mBeginningMinutes-1;
						int end = mEndingHours*60+mEndingMinutes-1;
						int current = hours*60 + minutes-1;
						if (current<start) {
							return new PairStatus(PAIR_STATUS_CURRENT_DAY_PAST);
						} else {
							if (current>end) {
								return new PairStatus(PAIR_STATUS_CURRENT_DAY_FUTURE);
							} else {
								return new PairStatus(PAIR_STATUS_CURRENT, current-start);
							}
						}
					}
				}
			}
		}
	}
}
