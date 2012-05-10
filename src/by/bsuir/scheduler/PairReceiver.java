package by.bsuir.scheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;

import by.bsuir.scheduler.activity.SchedulerActivity;
import by.bsuir.scheduler.model.DBAdapter;
import by.bsuir.scheduler.model.Day;
import by.bsuir.scheduler.model.Pair;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PairReceiver extends BroadcastReceiver {
	public static final String DAY="day";
	public static final String PAIR_NUMBER = "pair_number";
	/*
	 * true - Начало
	 * False - конец пары.
	 * Сделано для облегчения доступа к базе
	 */
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
		mAdapter = DBAdapter.getInstance(mContext.getApplicationContext());
		PendingIntent pi = existAlarm(mContext, NOTIFICATION_ID);
		if (pi!=null) {
			pi.cancel();
		}
		getNextAction();
		setNextNotification();
		setNextAlarm();
	}
	
	/**
	 * Тут всё забираем из базы. Уведомления кидаются в конце и начале пары. Надо ещё проверять, будет ли следующим будильник, будильник будет хранится один в SharedPref. 
	 * В паре можно добавить метод, который будет возвращать время в миллисекундах начала и конца (клонируем day и выставляем часы и минуты)
	 */
	private void getNextAction(){
		mDay = (GregorianCalendar)Calendar.getInstance();
		mPairs = mAdapter.getNextPairs(mDay);
	}
	
	/**
	 * Тут выкидываем уведомление. Оно ведёт при нажатии на SchedulerActivity. Уведомление NO_clearable
	 */
	private void setNextNotification(){
		NotificationManager nm = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_course, mPairs[0].getLesson()+" "+mPairs[0].getRoom(), System.currentTimeMillis());
		notification.flags = Notification.FLAG_NO_CLEAR;
		notification.defaults = Notification.DEFAULT_ALL;
		//Notification.FLAG_NO_CLEAR - использовать его
		Intent notifyIntent = new Intent(mContext.getApplicationContext(), SchedulerActivity.class);
		notifyIntent.setAction(Intent.ACTION_VIEW);
		notifyIntent.putExtra(GridCellAdapter.DAY, mPairs[0].getDate().getTimeInMillis());
		PendingIntent nPendingIntent = PendingIntent.getActivity(mContext.getApplicationContext(), NOTIFICATION_ID, notifyIntent, 0);
		notification.setLatestEventInfo(mContext.getApplicationContext(), mPairs[0].getLesson() + " до "+mPairs[0].endingTimeS(), mPairs[1].getLesson()+" c "+mPairs[1].beginningTime(), nPendingIntent);
		nm.notify(NOTIFICATION_ID, notification);
	}
	
	/**
	 * Выставляем следующий аларм в аларм-менеджере. При срабатывании кидает новый бродкаст.
	 */
	private void setNextAlarm(){
		AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
		PendingIntent intent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, new Intent(mContext.getApplicationContext(),PairReceiver.class), 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, mPairs[0].getEndTimeMillis(), intent);
	}
	
	/**
	 * Выбираем из висящих уведомлений наше
	 * @param id
	 * @return
	 */
	public static PendingIntent existAlarm(Context context, int id) {
		Intent intent = new Intent(context.getApplicationContext(), SchedulerActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		PendingIntent test = PendingIntent.getActivity(context,id , intent, PendingIntent.FLAG_NO_CREATE);
		return test;
	}
}
