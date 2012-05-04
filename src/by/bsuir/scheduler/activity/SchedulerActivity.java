package by.bsuir.scheduler.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import by.bsuir.scheduler.DayPagerAdapter;
import by.bsuir.scheduler.GridCellAdapter;
import by.bsuir.scheduler.R;
import by.bsuir.scheduler.model.DBAdapter;
import by.bsuir.scheduler.parser.Parser;
import by.bsuir.scheduler.parser.ParserListiner;

public class SchedulerActivity extends Activity {
	public static final int RESULT_DAY = 2;
	private DayPagerAdapter dayPagerAdapter;
	private ViewPager viewPager;
	private DBAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = DBAdapter.getInstance(getApplicationContext());
		if (mAdapter.isFilling()) {
			dayPagerAdapter = new DayPagerAdapter(this, System.currentTimeMillis());
			viewPager = new ViewPager(this);
			viewPager.setAdapter(dayPagerAdapter);
			viewPager.setCurrentItem(DayPagerAdapter.POSITION, false);
			setContentView(viewPager);
		}
		

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("SchedulerActivity", "onActivityResult");
		if (resultCode == RESULT_DAY) {
			dayPagerAdapter = new DayPagerAdapter(this, data.getLongExtra(GridCellAdapter.DAY, System.currentTimeMillis()));
			viewPager = new ViewPager(this);
			viewPager.setAdapter(dayPagerAdapter);
			viewPager.setCurrentItem(DayPagerAdapter.POSITION, false);
			setContentView(viewPager);
		} else super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_item_day:
			return true;

		case R.id.menu_item_week:
			//
			return true;

		case R.id.menu_item_month:
			if (mAdapter.isFilling()) {
				intent = new Intent(this, MonthActivity.class);
				startActivityForResult(intent, RESULT_DAY);
			}
			return true;
		case R.id.menu_item_refresh:
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			DBAdapter.getInstance(getApplicationContext()).refreshSchedule(prefs.getString(getString(R.string.group_number), ""+(-1)
					), Integer.parseInt(prefs.getString(getString(R.string.preference_sub_group_list),""+ 0)), new ParserListiner() {
				
				@Override
				public void onException(Exception e) {
					Toast.makeText(getApplicationContext(), "Очевидно, что-то пошло не так :(", Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void onComplete() {
					// TODO Auto-generated method stub
					
				}
			});
			//FIXEME всё это в onComplete + возвращаться в тот день, который был текущим 
			dayPagerAdapter = new DayPagerAdapter(this, System.currentTimeMillis());
			viewPager = new ViewPager(this);
			viewPager.setAdapter(dayPagerAdapter);
			viewPager.setCurrentItem(DayPagerAdapter.POSITION, false);
			setContentView(viewPager);
			return true;

		case R.id.menu_item_preferences:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;

		case R.id.menu_item_info_details:
			intent = new Intent(this, InfoActivity.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}