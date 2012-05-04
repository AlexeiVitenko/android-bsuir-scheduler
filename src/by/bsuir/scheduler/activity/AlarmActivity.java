package by.bsuir.scheduler.activity;

import by.bsuir.scheduler.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;

public class AlarmActivity extends PreferenceActivity {

	public static final String ALARM_PREF = "alarm_pref";
	private String ALARM_TYPE = "pref_alarm_type";
	private String ALARM_LESSON = "pref_alarm_before_lesson";
	private String ALARM_TIME = "pref_alarm_time";
	private String ALARM_RINGTONE = "pref_alarm_ringtone";
	private String ALARM_VOLUME = "pref_alarm_volume";
	private String ALARM_VIBRATION = "pref_alarm_vibration";

	private ListPreference alarmType;
	private ListPreference alarmBeforeLesson;
	private Preference alarmTime;
	private RingtonePreference alarmRingtone;
	private Preference alarmVolume;
	private CheckBoxPreference alarmVibration;

	private SharedPreferences sharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.alarm);
		sharedPref = getSharedPreferences(ALARM_PREF, MODE_PRIVATE);
		
		alarmType = (ListPreference) findPreference(ALARM_TYPE);
		alarmBeforeLesson = (ListPreference) findPreference(ALARM_LESSON);
		alarmTime = findPreference(ALARM_TIME);
		alarmRingtone = (RingtonePreference) findPreference(ALARM_RINGTONE);
		alarmVolume = findPreference(ALARM_VOLUME);
		alarmVibration = (CheckBoxPreference) findPreference(ALARM_VIBRATION);

		final String[] alarmTypes = getResources().getStringArray(R.array.alarm_types);
		if (sharedPref.contains(ALARM_TYPE)) {
			int indexOfType = sharedPref.getInt(ALARM_TYPE, 0);
			alarmType.setSummary(alarmTypes[indexOfType]);
		}
		alarmBeforeLesson.setEnabled(Integer.parseInt(alarmType.getValue()) == 1);
		alarmType.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						int value = Integer.parseInt((String) newValue);
						alarmType.setValue((String) newValue);
						alarmType.setSummary(alarmTypes[value]);
						
						alarmBeforeLesson.setEnabled(value == 1);
						return false;
					}
				});
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		//длждлж
		Editor editor = sharedPref.edit();
		editor.putInt(ALARM_TYPE, Integer.parseInt(alarmType.getValue()));
		editor.commit();
	}

}
