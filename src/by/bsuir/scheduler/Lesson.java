package by.bsuir.scheduler;

public class Lesson {
	private int id;
	private int mWeek;
	private int mDay;
	private String mLesson;
	private String mPrepod;
	private String mRoom;
	private int mSubGroup;
	private int mType;
	private int mBeginninHours = -1;
	private int mBeginninMinutes = -1;
	private int mEndingHours = -1;
	private int mEndingMinutes = -1;
	private String note;

	public Lesson(int id, String day, String week, String time,
			String subGroup, String lesson, String type, String room,
			String prepod) {
		// 0 - общая

		this.id = id;
		this.note = type + " " + week;

		if (subGroup.equals("")) {
			mSubGroup = 0;
		} else {
			mSubGroup = Integer.parseInt(subGroup);
		}
		if (!time.equals("")) {
			String[] times = time.split("[:-]");
			mBeginninHours = Integer.parseInt(times[0]);
			mBeginninMinutes = Integer.parseInt(times[1]);
			mEndingHours = Integer.parseInt(times[2]);
			mEndingMinutes = Integer.parseInt(times[3]);
		}
		// 0 - всегда
		if (week.equals("")) {
			mWeek = 0;
		} else {
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
			if (type.equals("кр")) {
				mType = 4;
			}
		}

		mLesson = lesson;
		mPrepod = prepod;
		mRoom = room;
	}

	@Override
	public String toString() {
		return "" + mWeek + '\t' + mBeginninHours + ":" + mBeginninMinutes
				+ "-" + mEndingHours + ":" + mEndingMinutes + '\t' + mSubGroup
				+ '\t' + mLesson + '\t' + mType + '\t' + mRoom + '\t' + mPrepod;
	}

	public int getID() {
		return id;
	}

	public String getTimeStart() {
		return "" + mBeginninHours + ":" + mBeginninMinutes;
	}

	public String getTimeEnd() {
		return "" + mEndingHours + ":" + mEndingMinutes;
	}

	public int getType() {
		return mType;
	}

	public String getSubject() {
		return mLesson;
	}

	public String getRoom() {
		return mRoom;
	}

	public String getTeacher() {
		return mPrepod;
	}

	public String getNote() {
		return note;
	}
}
