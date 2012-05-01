package by.bsuir.scheduler.parser;

public class Lesson {
	private int mWeek;
	private int mDay;
	private String mLesson;
	private String mPrepod;
	private String mRoom;
	private int mSubGroup;
	private int mType;
	private int mBeginningHours = -1;
	private int mBeginningMinutes = -1;
	private int mEndingHours = -1;
	private int mEndingMinutes = -1;
	
	public int getWeek(){
		return mWeek;
	}
	
	public int getDay(){
		return mDay;
	}
	
	public String getLesson(){
		return mLesson;
	}
	
	public String getTeacher() {
		return mPrepod;
	}
	
	public String getRoom() {
		return mRoom;
	}
	
	public int getSubGroup() {
		return mSubGroup;
	}
	
	public int getType() {
		return mType;
	}
	
	public int getBeginningHours(){
		return mBeginningHours;
	}
	public int getBeginningMinutes(){
		return mBeginningMinutes;
	}
	public int getEndingHours(){
		return mEndingHours;
	}
	public int getEndingMinutes(){
		return mEndingMinutes;
	}
	
	public Lesson(String day, String week, String time, String subGroup, String lesson, String type, String room, String prepod){
		mDay = 0;
		if (day.equals("пн")) {
			mDay = 1;
		}
		if (day.equals("вт")) {
			mDay = 2;
		}
		if (day.equals("ср")) {
			mDay = 3;
		}
		if (day.equals("чт")) {
			mDay = 4;
		}
		if (day.equals("пт")) {
			mDay = 5;
		}
		if (day.equals("сб")) {
			mDay = 6;
		}
		
		//0 - общая
		if (subGroup.equals("")) {
			mSubGroup = 0;
		} else {
			mSubGroup = Integer.parseInt(subGroup);
		}
		if (!time.equals("")) {
			String[] times = time.split("[:-]");
			mBeginningHours = Integer.parseInt(times[0]);
			mBeginningMinutes = Integer.parseInt(times[1]);
			mEndingHours = Integer.parseInt(times[2]);
			mEndingMinutes = Integer.parseInt(times[3]);	
		}
		//0 - всегда
		if (week.equals("")) {
			mWeek = 0;
		}else{
			mWeek = Integer.parseInt(week);
		}
		
		if (type.equals("")) {
			mType = 0;
		} else {
			if (type.equals("лк")) {
				mType = 1; 
			}
			if (type.equals("пз")) {
				mType = 2;
			}
			if (type.equals("лр")) {
				mType = 3;
			}
			if (type.equals("кп")) {
				mType = 4;
			}
		}

		mLesson = lesson;
		mPrepod = prepod;
		mRoom = room;
	}
	
	@Override
	public String toString() {
		return ""+mWeek+'\t'+mBeginningHours+":"+mBeginningMinutes+"-"+mEndingHours+":"+mEndingMinutes+
				'\t'+mSubGroup+'\t'+mLesson+'\t'+mType+'\t'+mRoom+'\t'+mPrepod;
	}
}
