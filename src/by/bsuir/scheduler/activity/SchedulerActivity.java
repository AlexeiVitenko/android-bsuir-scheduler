package by.bsuir.scheduler.activity;

import java.util.GregorianCalendar;

import by.bsuir.scheduler.DayPagerAdapter;
import by.bsuir.scheduler.GridCellAdapter;

import by.bsuir.scheduler.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SchedulerActivity extends Activity {
	public static final int RESULT_DAY = 2;
	private DayPagerAdapter dayPagerAdapter;
	private ViewPager viewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dayPagerAdapter = new DayPagerAdapter(this, System.currentTimeMillis());

		viewPager = new ViewPager(this);
		viewPager.setAdapter(dayPagerAdapter);
		viewPager.setCurrentItem(dayPagerAdapter.POSITION, false);
		setContentView(viewPager);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("SchedulerActivity", "onActivityResult");
		if (resultCode == RESULT_DAY) {
			dayPagerAdapter = new DayPagerAdapter(this, data.getLongExtra(GridCellAdapter.DAY, System.currentTimeMillis()));
			viewPager.setAdapter(dayPagerAdapter);
			viewPager.setCurrentItem(dayPagerAdapter.POSITION, false);
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
			intent = new Intent(this, MonthActivity.class);
			startActivityForResult(intent,RESULT_DAY);
			return true;

		case R.id.menu_item_refresh:
			//
			return true;

		case R.id.menu_item_preferences:
			intent = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent, MonthActivity.GET_DAY);
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