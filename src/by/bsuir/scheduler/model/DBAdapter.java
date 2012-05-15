package by.bsuir.scheduler.model;

import java.io.Closeable;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;
import by.bsuir.scheduler.R;
import by.bsuir.scheduler.parser.Lesson;
import by.bsuir.scheduler.parser.Parser;
import by.bsuir.scheduler.parser.ParserListiner;
import by.bsuir.scheduler.parser.Pushable;

/**
 * 
 * @author Alexei
 * 
 */
public class DBAdapter implements Pushable, Closeable {
	public enum DayMatcherConditions {
		OVERFLOW_LEFT, OVERFLOW_RIGTH, FIRST_DAY, LAST_DAY, HOLYDAY, WORK_DAY
	}

	protected static DBAdapter mInstance;

	public static DBAdapter getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DBAdapter(context);
		}
		return mInstance;
	}

	private GregorianCalendar septFirst;
	private GregorianCalendar mStartDay;
	private GregorianCalendar mLastDay;

	public long getStartTimeMillis() {
		return mStartDay.getTimeInMillis();
	}

	public long getLastDayMillis() {
		return mLastDay.getTimeInMillis();
	}

	private Context mContext;
	private DBHelper mDBHelper;
	private String[] daysOfWeek;

	protected DBAdapter(Context context) {
		mContext = context;
		mDBHelper = new DBHelper(context);
		daysOfWeek = mContext.getResources().getStringArray(
				R.array.days_of_week);
		if (isFilling()) {
			recalculateSomeThings();
		}
	}

	/**
	 * Возвращает структуру типа Day.
	 * 
	 * @param day
	 */
	public Day getDay(GregorianCalendar day) {
		int week = getWeekNumber(day);
		Cursor cursor = getDay(day.get(GregorianCalendar.DAY_OF_WEEK), week);
		return new Day(day, cursor, this, week);
	}

	/**
	 * Проверяет, является ли день учебным. Необходимо для календаря месяца и
	 * подгрузки дней.
	 * 
	 * @param day
	 * @return true - если да.
	 */
	public boolean isWorkDay(GregorianCalendar day) {
		Cursor cursor = getDay(day.get(GregorianCalendar.DAY_OF_WEEK),
				getWeekNumber(day));
		boolean is = cursor.getCount() > 0;
		cursor.close();
		return is;
	}

	/**
	 * Высчитывается какая неделя.
	 * отсчёт идёт от недели, на которой расположено 1-е сентября
	 * @param day
	 * @return
	 */
	private int getWeekNumber(GregorianCalendar day) {
		int weeks = 0;
		if (day.get(Calendar.YEAR) > septFirst.get(Calendar.YEAR)) {
			if (day.getMinimalDaysInFirstWeek() != 7) {
				weeks--;
			}
			weeks += (new GregorianCalendar(2011, 11, 31)
					.get(Calendar.WEEK_OF_YEAR) - septFirst
					.get(Calendar.WEEK_OF_YEAR)) + 1;
			weeks += day.get(Calendar.WEEK_OF_YEAR);
		} else {
			weeks = day.get(Calendar.WEEK_OF_YEAR)
					- septFirst.get(Calendar.WEEK_OF_YEAR) + 1;
		}
		return weeks % 4 + 1;
	}

	//FIXME эти методы различаются только запросом, поэтому надо бы сделать перегрузку.
	public Pair getPair(GregorianCalendar date) {
		Pair pair = getPairFromCursor(mDBHelper.getWritableDatabase().query(
				DBHelper.SCHEDULE_VIEW_NAME,
				new String[] { BaseColumns._ID, DBColumns.DAY, DBColumns.WEEK,
						DBColumns.VIEW_SUBJECT, DBColumns.SUBJECT_TYPE,
						DBColumns.ROOM, DBColumns.SUBGROUP,
						DBColumns.VIEW_TEACHER, DBColumns.START_HOUR,
						DBColumns.START_MINUTES, DBColumns.END_HOUR,
						DBColumns.END_MINUTES },
				DBColumns.DAY + " = ? AND " + DBColumns.START_HOUR
						+ " = ? AND " + DBColumns.WEEK + " IN (?,0) ",
				new String[] { "" + date.get(Calendar.DAY_OF_WEEK),
						"" + date.get(Calendar.HOUR_OF_DAY),
						"" + getWeekNumber(date) }, null, null, null), date);
		pair.setDate(date);
		return pair;
	}

	public Pair getPair(GregorianCalendar date, int schedulerID) {
		Pair pair = getPairFromCursor(mDBHelper.getWritableDatabase().query(
				DBHelper.SCHEDULE_VIEW_NAME,
				new String[] { BaseColumns._ID, DBColumns.DAY, DBColumns.WEEK,
						DBColumns.VIEW_SUBJECT, DBColumns.SUBJECT_TYPE,
						DBColumns.ROOM, DBColumns.SUBGROUP,
						DBColumns.VIEW_TEACHER, DBColumns.START_HOUR,
						DBColumns.START_MINUTES, DBColumns.END_HOUR,
						DBColumns.END_MINUTES },
				BaseColumns._ID + " = ? AND " + DBColumns.DAY + " = ? AND "
						+ DBColumns.START_HOUR + " = ? AND " + DBColumns.WEEK
						+ " IN (?,0) ",
				new String[] { "" + schedulerID,
						"" + date.get(Calendar.DAY_OF_WEEK),
						"" + date.get(Calendar.HOUR_OF_DAY),
						"" + getWeekNumber(date) }, null, null, null), date);
		return pair;
	}
	
	public Pair getPair(int schedulerID) {
		return getPairFromCursor(mDBHelper.getWritableDatabase().query(
				DBHelper.SCHEDULE_VIEW_NAME,
				new String[] { BaseColumns._ID, DBColumns.DAY, DBColumns.WEEK,
						DBColumns.VIEW_SUBJECT, DBColumns.SUBJECT_TYPE,
						DBColumns.ROOM, DBColumns.SUBGROUP,
						DBColumns.VIEW_TEACHER, DBColumns.START_HOUR,
						DBColumns.START_MINUTES, DBColumns.END_HOUR,
						DBColumns.END_MINUTES },
				BaseColumns._ID + " = ?",
				new String[] { "" + schedulerID }, null, null, null),new GregorianCalendar(Locale.getDefault()));
	}
	
	private Pair getPairFromCursor(Cursor data, GregorianCalendar date){
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
		Pair p = (new Pair(this, date, data.getInt(WEEK),
				data.getInt(SUBGROUP), data.getString(SUBJECT),
				data.getInt(SUBJECT_TYPE), data.getString(ROOM),
				data.getString(TEACHER), new int[] { data.getInt(START_HOUR),
						data.getInt(START_MINUTES), data.getInt(END_HOUR),
						data.getInt(END_MINUTES) }, -1, data.getInt(ID)));
		data.close();
		return p;
	}

	public void recalculateSomeThings() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		int weeks = Integer.parseInt(pref.getString(
				mContext.getString(R.string.semester_length_weeks), "" + 18));
		long startTime = pref.getLong(
				mContext.getString(R.string.semester_start_day), -1);
		mStartDay = new GregorianCalendar(Locale.getDefault());
		mStartDay.setTimeInMillis(startTime);
		mStartDay = new GregorianCalendar(mStartDay.get(Calendar.YEAR),
				mStartDay.get(Calendar.MONTH),
				mStartDay.get(Calendar.DAY_OF_MONTH));
		mLastDay = new GregorianCalendar(Locale.getDefault());
		mLastDay.setTimeInMillis(mStartDay.getTimeInMillis());
		mLastDay.add(Calendar.WEEK_OF_YEAR, weeks);
		mLastDay.add(Calendar.DAY_OF_YEAR, -1);

		if (mStartDay.get(Calendar.MONTH) < 8) {
			septFirst = new GregorianCalendar(mStartDay.get(Calendar.YEAR) - 1,
					9, 1);
		} else {
			septFirst = new GregorianCalendar(mStartDay.get(Calendar.YEAR), 9,
					1);
		}

		Log.i("DBAdapter",
				"firstMonth = " + mStartDay.get(GregorianCalendar.MONTH)
						+ "lastMonth = "
						+ mLastDay.get(GregorianCalendar.MONTH));
	}

	public Cursor getDay(int dayOfWeek, int week) {
		return mDBHelper.getWritableDatabase().query(
				DBHelper.SCHEDULE_VIEW_NAME,
				new String[] { BaseColumns._ID, DBColumns.DAY, DBColumns.WEEK,
						DBColumns.VIEW_SUBJECT, DBColumns.SUBJECT_TYPE,
						DBColumns.ROOM, DBColumns.SUBGROUP,
						DBColumns.VIEW_TEACHER, DBColumns.START_HOUR,
						DBColumns.START_MINUTES, DBColumns.END_HOUR,
						DBColumns.END_MINUTES },
				DBColumns.DAY + " = ? AND " + DBColumns.WEEK + " IN (?,0)",
				new String[] { "" + dayOfWeek, "" + week }, null, null,
				DBColumns.START_HOUR);
	}

	/**
	 * Проверяет, входит ли день в учебный промежуток. Необходимо для
	 * недопущения перехода на те дни, когда и учёбы то нету
	 * 
	 * @param day
	 * @return состояние @see {@link DayMatcherConditions}
	 */
	public DayMatcherConditions dayMatcher(GregorianCalendar day) {
		if ((day.getTimeInMillis() - mStartDay.getTimeInMillis()) >= 0
				&& (day.getTimeInMillis() - mStartDay.getTimeInMillis()) < 86400000) {
			return DayMatcherConditions.FIRST_DAY;
		}
		if ((day.getTimeInMillis() - mLastDay.getTimeInMillis()) >= 0
				&& (day.getTimeInMillis() - mLastDay.getTimeInMillis()) < 86400000) {
			return DayMatcherConditions.LAST_DAY;
		}
		if (day.getTimeInMillis() < mStartDay.getTimeInMillis()) {
			return DayMatcherConditions.OVERFLOW_LEFT;
		}
		if (day.getTimeInMillis() > mLastDay.getTimeInMillis()) {
			return DayMatcherConditions.OVERFLOW_RIGTH;
		}
		if (!isWorkDay(day)) {
			return DayMatcherConditions.HOLYDAY;
		} else {
			return DayMatcherConditions.WORK_DAY;
		}
	}

	void changeNote(GregorianCalendar day, int scheduleId, String note) {
		mDBHelper.updateNote(scheduleId, note, day);
	}

	String getNote(GregorianCalendar day, int scheduleId) {
		return mDBHelper.getNote(scheduleId, day);
	}

	public void refreshSchedule(String group, int subGroup,
			ParserListiner listiner) {
		if (group.equals("-1")) {
			Toast.makeText(mContext, "Введите группу в настройках.",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (((ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo() == null
				|| !((ConnectivityManager) mContext
						.getSystemService(Context.CONNECTIVITY_SERVICE))
						.getActiveNetworkInfo().isConnected()) {
			listiner.onException(new Exception(
					"Отсутсвует подключение к интеренту"));
			return;
		}
		Log.d("Parsing", "Start");
		Parser p = new Parser(group, subGroup, this, listiner);
		if(p.prepare()){
			mDBHelper.dropTables(mDBHelper.getWritableDatabase());
			mDBHelper.close();
			long startTime = System.currentTimeMillis();
			p.parseSchedule();
			Log.d("Parse time", "" + (System.currentTimeMillis() - startTime));
		}else{
			listiner.onException(new Exception("No such group"));
		}
	}

	public boolean isFilling() {
		try{
			Cursor c = mDBHelper.getWritableDatabase()
					.query(DBHelper.SCHEDULE_VIEW_NAME, null, null, null, null,
							null, null);
			boolean is = c.getCount() > 0;
			c.close();
			return is;
		}catch (Exception e) {
			return false;
		}
	}

	@Override
	/**
	 * Наш адаптер также будет принимать пары от парсера. В этом ему поможет этот метод
	 */
	public void push(Lesson lesson) {
		mDBHelper.addScheduleItem(lesson.getLesson(), lesson.getType(),
				lesson.getBeginningHours(), lesson.getBeginningMinutes(),
				lesson.getEndingHours(), lesson.getEndingMinutes(),
				lesson.getDay(), lesson.getWeek(), lesson.getRoom(),
				lesson.getTeacher(), lesson.getSubGroup());
	}

	@Override
	public void close() throws IOException {
		mDBHelper.close();
	}

	// Беру первый и послдений месяцы и дни. Слава
	public GregorianCalendar getFirstDay() {
		return mStartDay;
	}

	public GregorianCalendar getLastDay() {
		return mLastDay;
	}

	public int getFirstMonth() {
		return mStartDay.get(GregorianCalendar.MONTH);
	}

	public int getLastMonth() {
		return mLastDay.get(GregorianCalendar.MONTH);
	}

	public int getYear() {
		return mLastDay.get(GregorianCalendar.YEAR);
	}

	public Pair[] getNextPairs(GregorianCalendar day) {
		Pair p0 = null;
		Pair p1 = null;

		Day d = getDay(day);
		int i;
		for (Pair pair : d.getPairs()) {
			int status = pair.getStatus().status;
			if (status == Pair.PAIR_STATUS_CURRENT) {
				p0 = pair;
			}
			if (status == Pair.PAIR_STATUS_CURRENT_DAY_FUTURE) {
				if (p0 == null) {
					p0 = pair.getPreviuosBreak(pair);
					p1 = pair;
					break;
				} else {
					p1 = pair.getPreviuosBreak();
					break;
				}
			}
		}
		if (p0 == null) {
			// TODO вот тут будет осуществляться подстановка будильника.
			do {
				day.add(Calendar.DAY_OF_MONTH, 1);
			} while (!isWorkDay(day));
			d = getDay(day);
			p1 = d.getPair(0);
			p0 = p1.getPreviuosBreak(p1);
		}
		if (p1 == null) {
			// TODO вот тут будет осуществляться подстановка будильника.
			do {
				day.add(Calendar.DAY_OF_MONTH, 1);
			} while (!isWorkDay(day));
			d = getDay(day);
			Pair p = d.getPair(0);
			p1 = p.getPreviuosBreak(p0);
		}
		return new Pair[] { p0, p1 };
	}
}
