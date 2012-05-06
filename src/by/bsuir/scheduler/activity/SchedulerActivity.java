package by.bsuir.scheduler.activity;

import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
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
		if (!mAdapter.isFilling()) {
			startActivityForResult(new Intent(this, ConfiguratorActivity.class),this.getClass().hashCode());
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (!mChooseMode) {
			if (mAdapter.isFilling()) {
				init(System.currentTimeMillis());
			}
		}else{
			mChooseMode = false;
		}
	}
	
	private boolean mChooseMode = false;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == this.getClass().hashCode()) {	
		switch (resultCode) {
			case RESULT_DAY:
				mChooseMode = true;			
				init(data.getLongExtra(GridCellAdapter.DAY, System.currentTimeMillis()));
				break;
			case ConfiguratorActivity.RESULT_PREFERENCES_CHANGES:
				mAdapter.recalculateSomeThings();
				parse();
				break;
			default:
				break;
			}
		} else super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
			mChooseMode = true;			
			init(System.currentTimeMillis());
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
			startActivity(intent);
			return true;

		case R.id.menu_item_week:
			//
			return true;

		case R.id.menu_item_month:
			if (mAdapter.isFilling()) {
				intent = new Intent(this, MonthActivity.class);
				startActivityForResult(intent, this.getClass().hashCode());
			}
			return true;
		case R.id.menu_item_refresh:
			parse();
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
	
	private void init(long time) {
		dayPagerAdapter = new DayPagerAdapter(this, time);
		viewPager = new ViewPager(this);
		viewPager.setAdapter(dayPagerAdapter);
		viewPager.setCurrentItem(DayPagerAdapter.POSITION, false);
		setContentView(viewPager);
	}
	
	private void parse(){
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.start_parsing));
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		AsyncTask<String, String, Boolean> asc = new AsyncTask<String, String, Boolean>(){
			private boolean succesfull = false;
			@Override
			protected Boolean doInBackground(String... params) {
				
				DBAdapter.getInstance(getApplicationContext()).refreshSchedule(prefs.getString(getString(R.string.group_number), ""+(-1)
						), Integer.parseInt(prefs.getString(getString(R.string.preference_sub_group_list),""+ 0)), new ParserListiner() {
					
					@Override
					public void onException(final Exception e) {
						pd.cancel();
						//Looper.prepare();
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), "Очевидно, что-то пошло не так :("+System.getProperty("LINE_SEPARATOR")+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
							}
						});
						succesfull = false;
						finish();
					}
					
					@Override
					public void onComplete() {
						pd.cancel();	
						init(System.currentTimeMillis());
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
		asc.execute(null);
		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		pd.show();
		//FIXEME всё это в onComplete + возвращаться в тот день, который был текущим 
	}
}