package by.bsuir.scheduler.activity;

import java.util.GregorianCalendar;
import java.util.Locale;

import by.bsuir.scheduler.DayPagerAdapter;
import by.bsuir.scheduler.MonthPagerAdapter;
import by.bsuir.scheduler.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MonthActivity extends Activity {
	public static final int GET_DAY = 1;
	public static final String EXTRA_MONTH = "month";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		init(intent);
	}

	private void init(Intent intent) {
		MonthPagerAdapter monthPagerAdapter = new MonthPagerAdapter(this);
		ViewPager viewPager = new ViewPager(this);
		viewPager.setAdapter(monthPagerAdapter);
		viewPager.setCurrentItem(monthPagerAdapter.getCurrentItem(intent
				.getIntExtra(EXTRA_MONTH,
						(new GregorianCalendar(Locale.getDefault()))
								.get(GregorianCalendar.MONTH))));
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
			finish();
			return true;

		case R.id.menu_item_week:
			//
			return true;

		case R.id.menu_item_month:
			intent = new Intent(this, MonthActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(MonthActivity.EXTRA_MONTH, (new GregorianCalendar(
					Locale.getDefault())).get(GregorianCalendar.MONTH));
			startActivityForResult(intent, SchedulerActivity.class.hashCode());
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
