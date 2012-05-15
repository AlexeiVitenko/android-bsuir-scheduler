package by.bsuir.scheduler;

import java.util.GregorianCalendar;
import java.util.Locale;

import by.bsuir.scheduler.activity.AlarmActivity;
import by.bsuir.scheduler.activity.AlarmClockActivity;
import by.bsuir.scheduler.model.DBAdapter;
import by.bsuir.scheduler.model.Day;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmClockReceiver extends BroadcastReceiver {

	public static final int ALARM_ID = 121;
	public static final String CHANGE = "change";
	public static final String CLOCK = "alarm_clock";
	public static final String ALARM_STATUS = "alarm_status";
	public static final String ALARM_TIME = "alarm_status";
	public static final String ALARM_LESSON_ID = "lesson_id";

	private Context context;
	private DBAdapter dbAdapter;
	private SharedPreferences alarmPref;
	private Day day;
	private GregorianCalendar time;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		alarmPref = PreferenceManager.getDefaultSharedPreferences(context);

		if (alarmPref.getBoolean(AlarmActivity.ALARM_CLOCK, false)) {
			Log.i("AlarmClockReceiver",
					"intent " + intent.getStringExtra(ALARM_STATUS));

			// если вызывается будильник
			//FIXME тут ругается на nullpointer
			if (intent.getStringExtra(ALARM_STATUS).equals(CLOCK)) {
				PowerManager.WakeLock wl;
				PowerManager pm = (PowerManager) context.getSystemService(context.POWER_SERVICE);
				wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Tag");
				wl.acquire();
				Log.d("wake","up");
				Intent it = new Intent(context, AlarmClockActivity.class);
				it.putExtras(intent.getExtras());
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(it);
			} else {
				if (intent.getStringExtra(ALARM_STATUS).equals(CHANGE)) {
					clearIntents(context);
				}
			}

			init();
			setNewAlarmClock();

		} else {
			clearIntents(context);
		}
	}

	private void init() {
		dbAdapter = DBAdapter.getInstance(context.getApplicationContext());
		time = new GregorianCalendar(Locale.getDefault());
		day = dbAdapter.getDay(time);
		time.setTimeInMillis(AlarmActivity.getAlarmTimeLong(context, day));

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
	}
	
	private void setNewAlarmClock() {
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context.getApplicationContext(),
				AlarmClockReceiver.class);
		intent.putExtra("МАГИЯ", "НО БЕЗ ЭТОЙ ХЕРНИ НЕ РАБОТАЕТ?!");
		intent.putExtra(ALARM_TIME, 0);
		intent.putExtra(ALARM_STATUS, CLOCK);

		if (Integer.parseInt(alarmPref.getString(AlarmActivity.ALARM_TYPE,""+ 0)) == 1) {
			int index = Integer.parseInt(alarmPref.getString(AlarmActivity.ALARM_TYPE,""+ 0));
			int maxIndex = day.getCount() - 1;
			if (index > maxIndex) {
				index = maxIndex;
			}
			intent.putExtra(ALARM_LESSON_ID, day.getPair(index).getId());
		}

		PendingIntent pi = PendingIntent.getBroadcast(
				context.getApplicationContext(), ALARM_ID, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pi);

		Toast.makeText(
				context,
				context.getResources().getString(R.string.app_name)
						+ ". "
						+ context.getResources()
								.getString(R.string.alarm_toast) + " "
						+ AlarmActivity.formatTime(time.getTimeInMillis()),
				Toast.LENGTH_LONG).show();
	}

	public static PendingIntent existAlarm(Context context, int id) {
		return PendingIntent.getBroadcast(context.getApplicationContext(), id,
				new Intent(context.getApplicationContext(),
						AlarmClockReceiver.class), 0);
	}

	public static void clearIntents(Context context) {
		PendingIntent pi = existAlarm(context, ALARM_ID);
		if (pi != null) {
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(pi);
			pi.cancel();
			Log.i("AlarmClockReceiver", "clearIntent");
		}
	}

}
