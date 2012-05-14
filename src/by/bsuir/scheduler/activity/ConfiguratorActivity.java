package by.bsuir.scheduler.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.prefs.Preferences;

import by.bsuir.scheduler.NonScrollableAdapter;
import by.bsuir.scheduler.R;
import by.bsuir.scheduler.activity.NonScrollableViewPager.OnInputCompleteListiner;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

public class ConfiguratorActivity extends Activity {
	private final ConfiguratorActivity mActivity = this;
	public static final int RESULT_PREFERENCES_CHANGES=666;
	
	private NonScrollableViewPager mPager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configurator);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		mPager = (NonScrollableViewPager)findViewById(R.id.nonScrollableViewPager1);
		LayoutInflater inflater = LayoutInflater.from(this);
		List<View> views = new ArrayList<View>();
		ScrollView l = (ScrollView)inflater.inflate(R.layout.configurator_0, null);
		((EditText)l.findViewById(R.id.config_0_group)).setText(preferences.getString(getString(R.string.group_number), ""));
		((Spinner)l.findViewById(R.id.config_0_spinner)).setSelection(Integer.parseInt(preferences.getString(getString(R.string.preference_sub_group_list),""+ 0)));
		views.add(l);
		l = (ScrollView)inflater.inflate(R.layout.configurator_1, null);
		((EditText)l.findViewById(R.id.config_1_weeks)).setText(preferences.getString(getString(R.string.semester_length_weeks), ""+17));
		DatePicker dp = (DatePicker)l.findViewById(R.id.config_1_date);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(preferences.getLong(getString(R.string.semester_start_day), System.currentTimeMillis()));
		dp.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
		views.add(l);
		l = (ScrollView)inflater.inflate(R.layout.configurator_2, null);
		views.add(l);
		final NonScrollableAdapter adapter = new NonScrollableAdapter(views);
		mPager.setOnInputCompleteListiner(new OnInputCompleteListiner() {
			
			@Override
			public void onInputComplete() {
				adapter.copleteInput(mActivity);
			}
		});
		mPager.setAdapter(adapter);
		//nsvp.setCurrentItem(0);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.config_back:
			mPager.prev();
			break;
		case R.id.config_next:
			mPager.next();
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
	}
	
	public void setResult(String group, int subGroup, String semesterLength, long date){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();
		editor.putString(getString(R.string.semester_length_weeks), semesterLength);
		editor.putString(getString(R.string.preference_sub_group_list), ""+subGroup);
		editor.putString(getString(R.string.group_number), group);
		editor.putLong(getString(R.string.semester_start_day), date);
		editor.commit();
		setResult(RESULT_PREFERENCES_CHANGES);
		finish();
	}
}
