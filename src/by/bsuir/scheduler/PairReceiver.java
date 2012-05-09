package by.bsuir.scheduler;

import by.bsuir.scheduler.activity.SchedulerActivity;
import by.bsuir.scheduler.model.DBAdapter;
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
	private static final int NOTIFICATION_ID = 1927;
	private Context mContext;
	private DBAdapter mAdapter;
	
	@Override
	/**
	 * Схема такая, AlarmManager кидает бродкаст. Мы его ловим. Отключаем уведомление, весящее в статусбаре (а оно нас отправляет на активити дня)
	 */
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		mAdapter = DBAdapter.getInstance(mContext.getApplicationContext());
		PendingIntent pi = existAlarm(NOTIFICATION_ID);
		if (pi!=null) {
			pi.cancel();
		}
	}
	
	/**
	 * Тут всё забираем из базы. Уведомления кидаются в конце и начале пары. Надо ещё проверять, будет ли следующим будильник, будильник будет хранится один в SharedPref. 
	 * В паре можно добавить метод, который будет возвращать время в миллисекундах начала и конца (клонируем day и выставляем часы и минуты)
	 */
	private void getNextAction(){
		//обращаемся в базу за след событием
	}
	
	/**
	 * Тут выкидываем уведомление. Оно ведёт при нажатии на SchedulerActivity. Уведомление NO_clearable
	 */
	private void setNextNotification(){
		NotificationManager nm = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		//тут надо заполнить уведомление
		//Notification.FLAG_NO_CLEAR - использовать его
	}
	
	/**
	 * Выставляем следующий аларм в аларм-менеджере. При срабатывании кидает новый бродкаст.
	 */
	private void setNextAlarm(){
		
	}
	
	/**
	 * Выбираем из висящих уведомлений наше
	 * @param id
	 * @return
	 */
	private PendingIntent existAlarm(int id) {
		Intent intent = new Intent(mContext.getApplicationContext(), SchedulerActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		PendingIntent test = PendingIntent.getActivity(mContext,NOTIFICATION_ID , intent, PendingIntent.FLAG_NO_CREATE);
		return test;
	}
}
