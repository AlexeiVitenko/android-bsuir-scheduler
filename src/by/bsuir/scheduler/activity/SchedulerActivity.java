package by.bsuir.scheduler.activity;

import by.bsuir.scheduler.DayPagerAdapter;

import by.bsuir.scheduler.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SchedulerActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DayPagerAdapter dayPagerAdapter = new DayPagerAdapter(this, System.currentTimeMillis());

		ViewPager viewPager = new ViewPager(this);
		viewPager.setAdapter(dayPagerAdapter);
		viewPager.setCurrentItem(dayPagerAdapter.POSITION, false);
		setContentView(viewPager);

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
			//
			return true;

		case R.id.menu_item_week:
			//
			return true;

		case R.id.menu_item_month:
			/*
			intent = new Intent(this, MonthActivity.class);
			startActivity(intent);
			*/
			return true;

		case R.id.menu_item_refresh:
			//
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