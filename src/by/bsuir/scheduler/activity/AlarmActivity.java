package by.bsuir.scheduler.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import by.bsuir.scheduler.AlarmClockReceiver;
import by.bsuir.scheduler.R;
import by.bsuir.scheduler.model.Day;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.RingtonePreference;
import android.widget.TimePicker;

public class AlarmActivity extends PreferenceActivity {

	public static final String TIME_FIRST_LESSON = "time_first_lesson";
	private static final int TIME_DIALOG = 10;
	private static final int VOLUME_DIALOG = 11;
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
			"HH:mm");
	//public static final String ALARM_PREF = "alarm_pref";
	public static final String ALARM_CLOCK = "pref_alarm_clock";
	public static final String ALARM_TYPE = "pref_alarm_type";
	public static final String ALARM_LESSON = "pref_alarm_before_lesson";
	public static final String ALARM_TIME = "pref_alarm_time";
	public static final String ALARM_RINGTONE = "pref_alarm_ringtone";
	public static final String ALARM_VOLUME = "pref_alarm_volume";
	public static final String ALARM_VIBRATION = "pref_alarm_vibration";

	private CheckBoxPreference alarmClock;
	private ListPreference alarmType;
	private ListPreference alarmBeforeLesson;
	private Preference alarmTime;
	private RingtonePreference alarmRingtone;
	private Preference alarmVolume;
	private CheckBoxPreference alarmVibration;

	private static SharedPreferences sharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.alarm);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);//getSharedPreferences(ALARM_PREF, MODE_PRIVATE);
		alarmClock = (CheckBoxPreference) findPreference(ALARM_CLOCK);
		alarmType = (ListPreference) findPreference(ALARM_TYPE);
		alarmBeforeLesson = (ListPreference) findPreference(ALARM_LESSON);
		alarmTime = findPreference(ALARM_TIME);
		alarmRingtone = (RingtonePreference) findPreference(ALARM_RINGTONE);
		alarmVolume = findPreference(ALARM_VOLUME);
		alarmVibration = (CheckBoxPreference) findPreference(ALARM_VIBRATION);

		final String[] alarmTypes = getResources().getStringArray(
				R.array.alarm_types);
		int indexOfType = sharedPref.getInt(ALARM_TYPE, 0);
		alarmType.setSummary(alarmTypes[indexOfType]);
		alarmBeforeLesson
				.setEnabled(Integer.parseInt(alarmType.getValue()) == 1);
		alarmType
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						int value = Integer.parseInt((String) newValue);
						alarmType.setValue((String) newValue);
						alarmType.setSummary(alarmTypes[value]);

						alarmBeforeLesson.setEnabled(value == 1);
						return true;
					}
				});

		// ЗАГЛУШКА
		int numberOfLessons = 5;
		//

		String[] summaryValues = getResources().getStringArray(
				R.array.alarm_summary_values);
		final CharSequence[] entries = new String[numberOfLessons];
		CharSequence[] entryValues = new String[numberOfLessons];
		for (int i = 0; i < numberOfLessons; i++) {
			entries[i] = summaryValues[i];
			entryValues[i] = "" + i;
		}
		alarmBeforeLesson.setEntries(entries);
		alarmBeforeLesson.setEntryValues(entryValues);
		int indexOfLesson = sharedPref.getInt(ALARM_LESSON, 0);
		alarmBeforeLesson.setSummary(summaryValues[indexOfLesson]);
		alarmBeforeLesson
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						int value = Integer.parseInt((String) newValue);
						alarmBeforeLesson.setValue((String) newValue);
						alarmBeforeLesson.setSummary(entries[value]);
						return true;
					}
				});
		alarmTime.setSummary(formatTime(sharedPref.getLong(ALARM_TIME,
				System.currentTimeMillis())));
		alarmTime.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(TIME_DIALOG);
				return false;
			}
		});

		String ringtoneURI = sharedPref.getString(ALARM_RINGTONE,
				android.provider.Settings.System.DEFAULT_RINGTONE_URI
						.toString());
		updateRingtonePref(alarmRingtone, ringtoneURI);
		alarmRingtone
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						updateRingtonePref((RingtonePreference) preference,
								(String) newValue);
						return false;
					}
				});

		alarmVolume
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						startActivityForResult(
								new Intent(
										android.provider.Settings.ACTION_SOUND_SETTINGS),
								0);
						return false;
					}
				});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG:
			final GregorianCalendar cal = new GregorianCalendar(
					Locale.getDefault());
			cal.setTimeInMillis(sharedPref.getLong(ALARM_TIME,
					System.currentTimeMillis()));
			TimePickerDialog dialog = new TimePickerDialog(this,
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							cal.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
							cal.set(GregorianCalendar.MINUTE, minute);
							Editor editor = sharedPref.edit();
							editor.putLong(ALARM_TIME, cal.getTimeInMillis());
							editor.commit();
							alarmTime.setSummary(formatTime(sharedPref.getLong(
									ALARM_TIME, System.currentTimeMillis())));
						}
					}, cal.get(GregorianCalendar.HOUR_OF_DAY),
					cal.get(GregorianCalendar.MINUTE), true);
			return dialog;
		case VOLUME_DIALOG:
			startActivityForResult(new Intent(
					android.provider.Settings.ACTION_SOUND_SETTINGS), 0);
		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Editor editor = sharedPref.edit();
		editor.putBoolean(ALARM_CLOCK, alarmClock.isChecked());
		editor.putInt(ALARM_TYPE, Integer.parseInt(alarmType.getValue()));
		editor.putInt(ALARM_LESSON,
				Integer.parseInt(alarmBeforeLesson.getValue()));
		editor.putBoolean(ALARM_VIBRATION, alarmVibration.isChecked());
		editor.commit();

		Intent intent = new Intent(getApplicationContext(), AlarmClockReceiver.class);
		intent.putExtra(AlarmClockReceiver.ALARM_STATUS, AlarmClockReceiver.CHANGE);
		sendBroadcast(intent);
	}

	public static String getAlarmTimeString(Context context, Day day) {
		return formatTime(calculateAlarmTime(context, day).getTimeInMillis());
	}

	public static long getAlarmTimeLong(Context context, Day day) {
		return calculateAlarmTime(context, day).getTimeInMillis();
	}

	private static GregorianCalendar calculateAlarmTime(Context context, Day day) {
		GregorianCalendar alarm = new GregorianCalendar(Locale.getDefault());
		if (day.getCount() > 0) {
			//SharedPreferences sharedPref = context.getSharedPreferences(ALARM_PREF, MODE_PRIVATE);
			sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			alarm.setTimeInMillis(day.getDate().getTimeInMillis());

			GregorianCalendar temp = new GregorianCalendar(Locale.getDefault());
			temp.setTimeInMillis(sharedPref.getLong(ALARM_TIME,
					System.currentTimeMillis()));

			alarm.set(GregorianCalendar.HOUR_OF_DAY,
					temp.get(GregorianCalendar.HOUR_OF_DAY));
			alarm.set(GregorianCalendar.MINUTE,
					temp.get(GregorianCalendar.MINUTE));
			alarm.set(GregorianCalendar.SECOND, 0);
			alarm.set(GregorianCalendar.MILLISECOND, 0);

			if (sharedPref.getInt(AlarmActivity.ALARM_TYPE, 0) == 1) {
				int index = sharedPref.getInt(AlarmActivity.ALARM_LESSON, 0);
				int maxIndex = day.getCount() - 1;
				if (index > maxIndex) {
					index = maxIndex;
				}
				int[] temptime = day.getPair(index).getTime();
				temptime[2] = temptime[0]
						- alarm.get(GregorianCalendar.HOUR_OF_DAY);
				temptime[3] = temptime[1] - alarm.get(GregorianCalendar.MINUTE);
				if (temptime[2] < 0 || (temptime[2] == 0 && temptime[3] < 0)) {
					temptime[2] = 0;
					temptime[3] = 0;
				}
				alarm.set(GregorianCalendar.HOUR_OF_DAY, temptime[2]);
				alarm.set(GregorianCalendar.MINUTE, temptime[3]);
			}
			
		}
		return alarm;
	}

	private void updateRingtonePref(RingtonePreference preference,
			String newValue) {
		Ringtone ringtone = RingtoneManager.getRingtone(this,
				Uri.parse((String) newValue));
		if (ringtone != null) {
			alarmRingtone.setSummary(ringtone.getTitle(this));
			Editor editor = sharedPref.edit();
			editor.putString(ALARM_RINGTONE, newValue);
			editor.commit();

		} else {
			alarmRingtone.setSummary("");
		}
	}

	public static String formatTime(long date) {
		return TIME_FORMAT.format(new Date(date));
	}
}
