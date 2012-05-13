package by.bsuir.scheduler.activity;

import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.LimitedViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import by.bsuir.scheduler.AlarmClockReceiver;
import by.bsuir.scheduler.DayPagerAdapter;
import by.bsuir.scheduler.GridCellAdapter;
import by.bsuir.scheduler.PairReceiver;
import by.bsuir.scheduler.R;
import by.bsuir.scheduler.model.DBAdapter;
import by.bsuir.scheduler.model.DBAdapter.DayMatcherConditions;
import by.bsuir.scheduler.parser.ParserListiner;

public class SchedulerActivity extends Activity implements OnSemesterParametersChangeListiner{
	public static final int RESULT_DAY = 2;
	private DayPagerAdapter dayPagerAdapter;
	private LimitedViewPager viewPager;
	private DBAdapter mAdapter;
	private boolean mChooseMode = false; //I'm lovin' it
	private GregorianCalendar day;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAdapter = DBAdapter.getInstance(getApplicationContext());
		if (!mAdapter.isFilling()) {
			startActivityForResult(
					new Intent(this, ConfiguratorActivity.class), this
							.getClass().hashCode());
			mChooseMode = true;
		}else{
			sendBroadcast(new Intent(getApplicationContext(), PairReceiver.class));
		}
		Intent intent = new Intent(getApplicationContext(), AlarmClockReceiver.class);
		intent.putExtra(AlarmClockReceiver.ALARM_STATUS, AlarmClockReceiver.CHANGE);
		sendBroadcast(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!mChooseMode) {
			if (mAdapter.isFilling()) {
				if (day == null) {
					day = new GregorianCalendar(Locale.getDefault());
				}
				init(day.getTimeInMillis());
			}
		} else {
			mChooseMode = false;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (day == null) {
			day = new GregorianCalendar(Locale.getDefault());
		} else {
			day = dayPagerAdapter.getCurrentDay();
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == this.getClass().hashCode()) {
			switch (resultCode) {
			case RESULT_DAY:
				mChooseMode = true;
				init(data.getLongExtra(GridCellAdapter.DAY,
						System.currentTimeMillis()));
				break;
			case ConfiguratorActivity.RESULT_PREFERENCES_CHANGES:
				mChooseMode = true;
				mAdapter.recalculateSomeThings();
				parse();
				break;
			default:
				break;
			}
		} else
			if (requestCode == SettingsActivity.CALL_CONFIG) {
				if ((resultCode & SettingsActivity.GROUP_CHANGES) > 0) {
					mChooseMode = true;
					parse();
				}else{
					if ((resultCode & SettingsActivity.SEMESTER_CHANGES)>0) {
						init(day.getTimeInMillis());
					}
				}
			}else
			super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mChooseMode = true;
		init(intent.getLongExtra(GridCellAdapter.DAY,
				System.currentTimeMillis()));
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
			intent = new Intent(this, SchedulerActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(MonthActivity.EXTRA_MONTH, System.currentTimeMillis());
			startActivity(intent);
			return true;

		case R.id.menu_item_week:
			//
			return true;

		case R.id.menu_item_month:
			if (mAdapter.isFilling()) {
				intent = new Intent(this, MonthActivity.class);
				intent.putExtra(MonthActivity.EXTRA_MONTH, dayPagerAdapter
						.getCurrentDay().getTimeInMillis());
				startActivityForResult(intent, this.getClass().hashCode());
			}
			return true;
		case R.id.menu_item_refresh:
			parse();
			return true;

		case R.id.menu_item_preferences:
			intent = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent,SettingsActivity.CALL_CONFIG);
			return true;

		case R.id.menu_item_info_details:
			intent = new Intent(this, InfoActivity.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void init(long time) {
		System.gc();
		if (day == null) {
			day = new GregorianCalendar(Locale.getDefault());
		}
		day.setTimeInMillis(time);
		if (mAdapter.dayMatcher(day)==DayMatcherConditions.OVERFLOW_LEFT) {
			day.setTimeInMillis(mAdapter.getStartTimeMillis());
		}
		if (mAdapter.dayMatcher(day)==DayMatcherConditions.OVERFLOW_RIGTH) {
			day.setTimeInMillis(mAdapter.getLastDayMillis());
		}
		dayPagerAdapter = new DayPagerAdapter(this, day.getTimeInMillis());
		viewPager = new LimitedViewPager(this);
		viewPager.setAdapter(dayPagerAdapter);
		viewPager.setCurrentItem(DayPagerAdapter.POSITION, false);
		if (PairReceiver.existNotification(getApplicationContext(), PairReceiver.NOTIFICATION_ID)==null)
			sendBroadcast(new Intent(getApplicationContext(), PairReceiver.class));
		setContentView(viewPager);
	}

	private void parse() {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.start_parsing));
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		AsyncTask<String, String, Boolean> asc = new AsyncTask<String, String, Boolean>() {
			private boolean succesfull = false;

			@Override
			protected Boolean doInBackground(String... params) {

				DBAdapter.getInstance(getApplicationContext()).refreshSchedule(
						prefs.getString(getString(R.string.group_number), ""
								+ (-1)),
						Integer.parseInt(prefs.getString(
								getString(R.string.preference_sub_group_list),
								"" + 0)), new ParserListiner() {

							@Override
							public void onException(final Exception e) {
								pd.cancel();
								// Looper.prepare();
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(
												getApplicationContext(),
												"Очевидно, что-то пошло не так :("
														+ System.getProperty("LINE_SEPARATOR")
														+ e.getClass(),
												Toast.LENGTH_LONG).show();
									}
								});
								succesfull = false;
								/*if (!mAdapter.isFilling())*/ {
									finish();
								}
							}

							@Override
							public void onComplete() {
								pd.cancel();
								runOnUiThread(new Runnable() {
									public void run() {
										init(System.currentTimeMillis());
									}
								});
								sendBroadcast(new Intent(getApplicationContext(), PairReceiver.class));
								succesfull = true;
							}
						});
				return succesfull;
			}

			@Override
			protected void onProgressUpdate(String... values) {
				pd.setTitle(values[0]);
				super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				pd.cancel();
				super.onPostExecute(result);
			}
		};
		asc.execute(new String[]{});
		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		pd.show();
		// FIXEME всё это в onComplete + возвращаться в тот день, который был
		// текущим
	}

	@Override
	public void onSemesterChanges() {
		init(day.getTimeInMillis());
	}
}