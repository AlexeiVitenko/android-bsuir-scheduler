package by.bsuir.scheduler.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import by.bsuir.scheduler.AlarmClockReceiver;
import by.bsuir.scheduler.R;
import by.bsuir.scheduler.model.DBAdapter;
import by.bsuir.scheduler.model.Pair;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlarmClockActivity extends Activity {
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("state", "telState");
			if (mediaPlayer!=null) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
			}
			if (vibrator!=null) {
				vibrator.cancel();
			}
	//		finish();
		}
	};
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
			"dd.MM.yyyy HH:mm");

	private DBAdapter dbAdapter;
	private SharedPreferences sharedPref;

	private TextView alarmTime;
	private LinearLayout pairLayout;
	private TextView subject;
	private TextView room;
	private TextView pairTime;
	private Button ok;

	private GregorianCalendar time;
	private Pair pair;
	private MediaPlayer mediaPlayer;
	private Vibrator vibrator;
	private PowerManager.WakeLock mWl;
	private KeyguardLock mKeyguardLock;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		mWl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Tag");
		mWl.acquire();
		
		
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

		mKeyguardLock = keyguardManager.newKeyguardLock("Keyguard_Lock_Test");
		mKeyguardLock.disableKeyguard();
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.alarm_clock_dialog);
		setTitle(R.string.app_name);
		
		dbAdapter = DBAdapter.getInstance(getApplicationContext());
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		time = new GregorianCalendar(Locale.getDefault());

		Intent intent = getIntent();
		if (Integer.parseInt(sharedPref.getString(AlarmActivity.ALARM_TYPE,""+0)) == 1) {
			pair = dbAdapter.getPair(intent.getIntExtra(
					AlarmClockReceiver.ALARM_LESSON_ID, -1));
		}
		time.setTimeInMillis(intent.getLongExtra(AlarmClockReceiver.ALARM_TIME,
				System.currentTimeMillis()));

		alarmTime = (TextView) findViewById(R.id.alarm_dialog_now_time);
		pairLayout = (LinearLayout) findViewById(R.id.alarm_dialog_pair_layout);
		subject = (TextView) findViewById(R.id.alarm_dialog_subject);
		room = (TextView) findViewById(R.id.alarm_dialog_room);
		pairTime = (TextView) findViewById(R.id.alarm_dialog_time);
		ok = (Button) findViewById(R.id.alarm_dialog_OK_button);

		mediaPlayer = new MediaPlayer();
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		try {
			mediaPlayer.setDataSource(sharedPref.getString(
					AlarmActivity.ALARM_RINGTONE, RingtoneManager
							.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
							.toString()));
			mediaPlayer.prepare();
			if (sharedPref.getBoolean(AlarmActivity.ALARM_VIBRATION, true)) {
				vibrator.vibrate(6000);
			}
			mediaPlayer.setLooping(true);
			mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ok.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mediaPlayer.stop();
				vibrator.cancel();
				finish();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(mReceiver, new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));
		alarmTime.setText(AlarmActivity.formatTime(time.getTimeInMillis()));
		if (Integer.parseInt(sharedPref.getString(AlarmActivity.ALARM_TYPE,""+0)) == 0) {
			pairLayout.setEnabled(false);
			subject.setHeight(0);
			room.setHeight(0);
			pairTime.setHeight(0);
		} else {
			subject.setText(pair.getLesson());
			room.setText(getResources().getString(R.string.room) + " "
					+ pair.getRoom());
			int[] times = pair.getTime();
			pairTime.setText(String.format("%2d:%02d - %2d:%02d", times[0],
					times[1], times[2], times[3]));
		}
	}
	
	@Override
	protected void onStop() {
		if (mWl!=null) {
			if (mWl.isHeld()) mWl.release();
		}
		if (mKeyguardLock!=null) {
			mKeyguardLock.reenableKeyguard();
		}
		if (mediaPlayer!=null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
		}
		if (vibrator!=null) {
			vibrator.cancel();
		}
		unregisterReceiver(mReceiver);
		super.onStop();
	}
}
