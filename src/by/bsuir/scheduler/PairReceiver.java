package by.bsuir.scheduler;

import by.bsuir.scheduler.activity.SchedulerActivity;
import by.bsuir.scheduler.model.DBAdapter;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PairReceiver extends BroadcastReceiver {
	public static final String DAY="day";
	public static final String PAIR_NUMBER = "pair_number"; 
	private static final int NOTIFICATION_ID = 1927;
	private Context mContext;
	private DBAdapter mAdapter;
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		mAdapter = DBAdapter.getInstance(mContext.getApplicationContext());
		PendingIntent pi = existAlarm(NOTIFICATION_ID);
		if (pi!=null) {
			pi.cancel();
		}
	}
	
	private void getNextAction(){
		//обращаемся в базу за след событием
	}
	
	private void setNextNotification(){
		NotificationManager nm = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		//тут надо заполнить уведомление
	}
	
	/**
	 * Выбираем из висящих уведомлений наше
	 * @param id
	 * @return
	 */
	private PendingIntent existAlarm(int id) {
		Intent intent = new Intent(mContext, SchedulerActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		PendingIntent test = PendingIntent.getActivity(mContext,NOTIFICATION_ID , intent, PendingIntent.FLAG_NO_CREATE);
		return test;
	}
}
