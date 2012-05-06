package by.bsuir.scheduler.activity;

import java.util.ArrayList;
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

public class ConfiguratorActivity extends Activity {
	private final ConfiguratorActivity mActivity = this;
	public static final int RESULT_PREFERENCES_CHANGES=666;
	
	private NonScrollableViewPager mPager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configurator);
		mPager = (NonScrollableViewPager)findViewById(R.id.nonScrollableViewPager1);
		LayoutInflater inflater = LayoutInflater.from(this);
		List<View> views = new ArrayList<View>();
		views.add(inflater.inflate(R.layout.configurator_0, null));
		views.add(inflater.inflate(R.layout.configurator_1, null));
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
