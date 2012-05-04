package by.bsuir.scheduler.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import by.bsuir.scheduler.R;
import by.bsuir.scheduler.model.DBAdapter;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.DatePicker;

public class SettingsActivity extends PreferenceActivity {
	private static final int DATE_DIALOG=1001;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	private Preference mSemesterLength;
	private Preference mStartDate;
	private EditTextPreference mGroupNumber;
	private ListPreference mSubGroup;
	private SharedPreferences mPref;
	private DBAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		mAdapter = DBAdapter.getInstance(getApplicationContext());
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		mSemesterLength = findPreference(getString(R.string.semester_length_weeks));
		mSemesterLength.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mSemesterLength.setSummary(""+mPref.getString(getString(R.string.semester_length_weeks),""+(-1))+getString(R.string.weeks));
				mAdapter.recalculateSomeThings();
				return false;
			}
		});
		
		mStartDate = findPreference(getString(R.string.semester_start_day));
		mStartDate.setSummary(formatDate(mPref.getLong(getString(R.string.semester_start_day), System.currentTimeMillis())));
		mStartDate.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(DATE_DIALOG);
				return false;
			}
		});
		
		mGroupNumber = (EditTextPreference) findPreference(getString(R.string.group_number));
		mGroupNumber.setSummary(mGroupNumber.getText());
		mGroupNumber.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary((String)newValue);
				return true;
			}
		});
		
		mSubGroup = (ListPreference)findPreference(getString(R.string.preference_sub_group_list));
		mSubGroup.setSummary(mSubGroup.getEntry());
		mSubGroup.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mSubGroup.setSummary(getResources().getStringArray(R.array.preferences_sub_group_entries)[Integer.parseInt((String)newValue)]);
				return true;
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG:
			GregorianCalendar gc = new GregorianCalendar(Locale.getDefault());
			gc.setTimeInMillis(mPref.getLong(getString(R.string.semester_start_day), System.currentTimeMillis()));
			DatePickerDialog dpd = new DatePickerDialog(this, new OnDateSetListener() {
				
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					GregorianCalendar gcc = new GregorianCalendar(year, monthOfYear, dayOfMonth);
					mStartDate.setSummary(formatDate(gcc.getTimeInMillis()));
					Editor edit = mPref.edit();
					edit.putLong(getString(R.string.semester_start_day), gcc.getTimeInMillis());
					edit.commit();
					mAdapter.recalculateSomeThings();
				}
			}, gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH), gc.get(GregorianCalendar.DAY_OF_MONTH));
			return dpd;

		default:
			return super.onCreateDialog(id);
		}
	}
	/*
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG:
			((DatePickerDialog)dialog).set
			break;
		default:
			super.onPrepareDialog(id, dialog);
			break;
		}
	}
	*/
	private static String formatDate(long date ){
		return dateFormat.format(new Date(date));
	}
}
