package by.bsuir.scheduler.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import by.bsuir.scheduler.R;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.DatePicker;

public class SettingsActivity extends PreferenceActivity {
	private static final int DATE_DIALOG=1001;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	private Preference mSemesterLength;
	private Preference mStartDate;
	private SharedPreferences mPref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		mSemesterLength = findPreference(getString(R.string.semester_length_weeks));
		mSemesterLength.setSummary(""+mPref.getString(getString(R.string.semester_length_weeks),""+(-1))+getString(R.string.weeks));
		
		mStartDate = findPreference(getString(R.string.semester_start_day));
		mStartDate.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(DATE_DIALOG);
				return false;
			}
		});
		mStartDate.setSummary(formatDate(mPref.getLong(getString(R.string.semester_start_day), System.currentTimeMillis())));
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG:
			GregorianCalendar gc = new GregorianCalendar();
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
