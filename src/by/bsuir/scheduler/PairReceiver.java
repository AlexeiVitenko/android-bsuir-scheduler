package by.bsuir.scheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;
import by.bsuir.scheduler.activity.AlarmActivity;
import by.bsuir.scheduler.activity.SchedulerActivity;
import by.bsuir.scheduler.model.DBAdapter;
import by.bsuir.scheduler.model.Day;
import by.bsuir.scheduler.model.Pair;

/**
 * 
 * @author Alexei
 *
 */
public class PairReceiver extends BroadcastReceiver {
	public static final String DAY = "day";
	public static final String PAIR_NUMBER = "pair_number";
	public static final String PAIR_STATUS = "pair_status";
	public static final int NOTIFICATION_ID = 1927;
	private Context mContext;
	private DBAdapter mAdapter;
	private Pair[] mPairs;
	private GregorianCalendar mDay;

	@Override
	/**
	 * Схема такая, AlarmManager кидает бродкаст. Мы его ловим. Отключаем уведомление, весящее в статусбаре (а оно нас отправляет на активити дня)
	 */
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		mAdapter = DBAdapter.getInstance(mContext.getApplicationContext());
		PendingIntent pi = existNotification(mContext, NOTIFICATION_ID);
		if (pi != null) {
			pi.cancel();
		}
		if (PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(
				mContext.getString(R.string.notifications_enabled), true)) 
			if (mAdapter.isFilling()){
				getNextAction();
				setNextNotification();
				setNextAlarm();
			}
		//setNextAlarmClock();
	}

	private void setNextAlarmClock() {
		GregorianCalendar tempDay = new GregorianCalendar(Locale.getDefault());
		Day day = mAdapter.getDay(tempDay);
		tempDay.setTimeInMillis(AlarmActivity.getAlarmTimeLong(mContext, day));

		Toast.makeText(
				mContext,
				"AlarmClock " + AlarmActivity.getAlarmTimeString(mContext, day),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Тут всё забираем из базы. Уведомления кидаются в конце и начале пары.
	 * Надо ещё проверять, будет ли следующим будильник, будильник будет
	 * хранится один в SharedPref. В паре можно добавить метод, который будет
	 * возвращать время в миллисекундах начала и конца (клонируем day и
	 * выставляем часы и минуты)
	 */
	private void getNextAction() {
		mDay = (GregorianCalendar) Calendar.getInstance();
		mPairs = mAdapter.getNextPairs(mDay);
	}

	/**
	 * Тут выкидываем уведомление. Оно ведёт при нажатии на SchedulerActivity.
	 * Уведомление NO_clearable
	 */
	private void setNextNotification() {
		NotificationManager nm = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_app,
				mPairs[0].getLesson() + " " + mPairs[0].getRoom(),
				System.currentTimeMillis());
		//notification.flags = Notification.FLAG_NO_CLEAR;
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		int counter = 0;
		int flag = 0;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		if (prefs.getBoolean(mContext.getString(R.string.notif_sound), false)) {
			counter++;
			flag |= Notification.DEFAULT_SOUND;
		}
		if (prefs.getBoolean(mContext.getString(R.string.notif_vibrate), false)) {
			counter++;
			flag |= Notification.DEFAULT_VIBRATE;
		}
		if (prefs.getBoolean(mContext.getString(R.string.notif_lights), false)) {
			counter++;
			flag |= Notification.DEFAULT_LIGHTS;
		}
		if (counter>=3) {
			notification.defaults = Notification.DEFAULT_ALL;
		}else{
			notification.defaults = flag;
		}
		
		Intent notifyIntent = new Intent(mContext.getApplicationContext(),
				SchedulerActivity.class);
		notifyIntent.setAction(Intent.ACTION_VIEW);
		notifyIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notifyIntent.putExtra(GridCellAdapter.DAY, mPairs[0].getDate()
				.getTimeInMillis());
		PendingIntent nPendingIntent = PendingIntent.getActivity(
				mContext.getApplicationContext(), NOTIFICATION_ID,
				notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		//Log.d("p0", mPairs[0].getLesson());
		//Log.d("p1null", "" + (mPairs[1] == null));
		//Log.d("p1", mPairs[1].getLesson());
		String current = mPairs[0].getLesson() + " до "
				+ mPairs[0].endingTimeS() + " " + mPairs[0].getRoom();
		Calendar d = Calendar.getInstance();
		if (mPairs[0].getDate().get(Calendar.DAY_OF_YEAR) > d
				.get(Calendar.DAY_OF_YEAR)) {
			current += " "
					+ mContext.getResources().getStringArray(
							R.array.days_of_week_abb)[mPairs[0].getDate().get(
							Calendar.DAY_OF_WEEK) - 1];
		}
		String next = mPairs[1].getLesson() + " c " + mPairs[1].beginningTime()
				+ " " + mPairs[1].getRoom();
		if (mPairs[1].getDate().get(Calendar.DAY_OF_YEAR) > d
				.get(Calendar.DAY_OF_YEAR)) {
			next += " "
					+ mContext.getResources().getStringArray(
							R.array.days_of_week_abb)[mPairs[1].getDate().get(
							Calendar.DAY_OF_WEEK) - 1];
		}
		notification.setLatestEventInfo(mContext.getApplicationContext(),
				current, next, nPendingIntent);
		nm.notify(NOTIFICATION_ID, notification);
	}

	/**
	 * Выставляем следующий аларм в аларм-менеджере. При срабатывании кидает
	 * новый бродкаст.
	 */
	private void setNextAlarm() {
		AlarmManager alarmManager = (AlarmManager) mContext
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent intent = PendingIntent
				.getBroadcast(mContext.getApplicationContext(),
						NOTIFICATION_ID,
						new Intent(mContext.getApplicationContext(),
								PairReceiver.class), 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, mPairs[0].getEndTimeMillis(),
				intent);
	}

	/**
	 * Выбираем из висящих уведомлений наше
	 * 
	 * @param id
	 * @return
	 */
	public static PendingIntent existNotification(Context context, int id) {
		Intent intent = new Intent(context.getApplicationContext(),
				SchedulerActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		PendingIntent test = PendingIntent.getActivity(context, id, intent,
				PendingIntent.FLAG_NO_CREATE);
		return test;
	}

	public static PendingIntent existAlarm(Context context, int id) {
		return PendingIntent
				.getBroadcast(context.getApplicationContext(), id, new Intent(
						context.getApplicationContext(), PairReceiver.class), 0);
	}

	public static void clearIntents(Context context) {
		PendingIntent pi = existNotification(context.getApplicationContext(),
				NOTIFICATION_ID);
		if (pi != null) {
			pi.cancel();
		}
		pi = existAlarm(context, NOTIFICATION_ID);
		if (pi != null) {
			pi.cancel();
		}
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_ID);
	}
}
