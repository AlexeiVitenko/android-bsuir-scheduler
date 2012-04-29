package by.bsuir.scheduler.model;

/**
 * 
 * @author alexei
 * //TODO добавить геттеров
 */
public class Pair{
	private int mScheduleId;
	private int mWeek;
	private String mLesson;
	private String mTeacher;
	private String mRoom;
	private String mStringType;
	private int mSubGroup;
	private int mType;
	private int mBeginningHours = -1;
	private int mBeginningMinutes = -1;
	private int mEndingHours = -1;
	private int mEndingMinutes = -1;
	private Day mDay;
	private String mNote;
	
	protected Pair(Day container, int week, int subGroup, String lesson, int type, String sType, String room, String teacher, int times[], String note, int schedule){
		mDay = container;
		mSubGroup = subGroup;
		mBeginningHours = times[0];
		mBeginningMinutes = times[1];
		mEndingHours = times[2];
		mEndingMinutes = times[3];	
		mStringType = sType;
		mWeek = week;
		mType = type;

		mLesson = lesson;
		mTeacher = teacher;
		mRoom = room;
		mNote = note;
		mScheduleId = schedule;
	}
	
	public String getNote(){
		return mNote;
	}
	
	public void setNote(String note){
		mNote = note;
		mDay.changeNote(mScheduleId, note);
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
	
	public String getStringType() {
		return mStringType;
	}
	
	public String getRoom(){
		return mRoom;
	}
	
	public String getTeacher() {
		return mTeacher;
	}
	
	public int[] getTime() {
		return new int[]{mBeginningHours,mBeginningMinutes,mEndingHours,mEndingMinutes};
	}
}
