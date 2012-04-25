package by.bsuir.scheduler.activity;

import java.util.GregorianCalendar;

import by.bsuir.scheduler.DayPagerAdapter;
import by.bsuir.scheduler.MonthPagerAdapter;

import by.bsuir.scheduler.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MonthActivity extends Activity {
	public static final int GET_DAY = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// вводим кол-во недель текущего сема и дату 2ого сема.
		MonthPagerAdapter monthPagerAdapter = new MonthPagerAdapter(17, 6,
				GregorianCalendar.FEBRUARY, this);
		ViewPager viewPager = new ViewPager(this);
		viewPager.setAdapter(monthPagerAdapter);
		viewPager.setCurrentItem(monthPagerAdapter.getCurrentItem());

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
			/*
			intent = new Intent(this, SchedulerActivity.class);
			startActivity(intent);*/
			finish();
			return true;

		case R.id.menu_item_week:
			//
			return true;

		case R.id.menu_item_month:
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
