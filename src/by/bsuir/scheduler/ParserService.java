package by.bsuir.scheduler;

import by.bsuir.scheduler.activity.SchedulerActivity;
import by.bsuir.scheduler.model.DBAdapter;
import by.bsuir.scheduler.parser.ParserListiner;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ParserService extends IntentService {
	private Object mMonitor = new Object();
	
	public ParserService() {
		super("Parser");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		Log.d("Service", "start");
		synchronized (mMonitor) {
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			new Thread(new Runnable() {
				
				@Override
				public void run() {

					DBAdapter.getInstance(getApplicationContext()).refreshSchedule(
							prefs.getString(getString(R.string.group_number), ""
									+ (-1)),
							Integer.parseInt(prefs.getString(
									getString(R.string.preference_sub_group_list),
									"" + 0)), new ParserListiner() {
			
								@Override
								public void onException(final Exception e) {
									sendBroadcast(new Intent(SchedulerActivity.PARSER_EXCEPTION));
									synchronized (mMonitor) {
										mMonitor.notifyAll();	
									}
								}
			
			
								@Override
								public void onComplete() {
									sendBroadcast(new Intent(SchedulerActivity.PARSER_ACTION));
									synchronized (mMonitor) {
										mMonitor.notifyAll();	
									}
								}
							});
				}
			}).start();
			try {
				mMonitor.wait();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.d("Service", "Get out");
		}
	}

}