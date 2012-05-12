package by.bsuir.scheduler;

import java.util.GregorianCalendar;
import java.util.Locale;

import by.bsuir.scheduler.activity.AlarmActivity;
import by.bsuir.scheduler.model.DBAdapter;
import by.bsuir.scheduler.model.Day;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class AlarmClockReceiver extends BroadcastReceiver {

	public static final int ALARM_ID = 121;
	public static final String CHANGE = "change";
	public static final String CLOCK = "alarm_clock";
	public static final String ALARM_STATUS = "alarm_status";
	public static final String ALARM_TIME = "alarm_status";
	public static final String ALARM_TYPE = "alarm_type";
	public static final String ALARM_LESSON_ID = "lesson_id";

	private Context context;
	private DBAdapter dbAdapter;
	private SharedPreferences alarmPref;
	private Day day;
	private GregorianCalendar time;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		
		// если вызывается будильник
		if (intent.getStringExtra(ALARM_STATUS) == CLOCK) {
			GregorianCalendar alarmTime = new GregorianCalendar(
					Locale.getDefault());
			AlarmClockDialog dialog = new AlarmClockDialog(context, intent);
		}

		init();
		setNewAlarmClock();
	}

	private void init() {
		dbAdapter = DBAdapter.getInstance(context.getApplicationContext());
		alarmPref = context.getSharedPreferences(AlarmActivity.ALARM_PREF,
				AlarmActivity.MODE_PRIVATE);
		time = new GregorianCalendar(Locale.getDefault());
		day = dbAdapter.getDay(time);
		time.setTimeInMillis(AlarmActivity.getAlarmTimeLong(context, day));
	}

	private void setNewAlarmClock() {
		GregorianCalendar now = new GregorianCalendar(Locale.getDefault());
		boolean isChange = false;
		// поиск ближайшего будильника
		while (!dbAdapter.isWorkDay(time)
				|| (now.getTimeInMillis() > time.getTimeInMillis())) {
			time.add(GregorianCalendar.DAY_OF_MONTH, 1);
			isChange = true;
		}

		if (isChange) {
			day = dbAdapter.getDay(time);
			time.setTimeInMillis(AlarmActivity.getAlarmTimeLong(context, day));
		}
		

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context.getApplicationContext(), AlarmClockReceiver.class);
		intent.putExtra(ALARM_TIME, time.getTimeInMillis());
		intent.putExtra(ALARM_STATUS, CLOCK);
		
		if (alarmPref.getInt(AlarmActivity.ALARM_TYPE, 0) == 1) {
			intent.putExtra(ALARM_TYPE, 1);
			int index = alarmPref.getInt(AlarmActivity.ALARM_LESSON, 0);
			int maxIndex = day.getCount() - 1;
			if (index > maxIndex) {
				index = maxIndex;
			}
			intent.putExtra(ALARM_LESSON_ID, day.getPair(index).getId());
		} else {
			intent.putExtra(ALARM_TYPE, 0);
		}
		
		PendingIntent pi = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pi);
		

		Toast.makeText(context, "БУДИЛЬНИК: " + AlarmActivity.formatTime(time.getTimeInMillis()), Toast.LENGTH_SHORT).show();
	}

}
