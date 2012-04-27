package by.bsuir.scheduler;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import by.bsuir.scheduler.R;

import by.bsuir.scheduler.activity.LessonActivity;
import by.bsuir.scheduler.model.DBAdapter;
import by.bsuir.scheduler.model.Day;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class DayPagerAdapter extends PagerAdapter {
	public static final int POSITION = 502;
	private static final int LOOPS = 300;

	private Context mContext;
	private LayoutInflater mInflater;
	// private List<View> mPages;
	private GregorianCalendar mCurrentDay;
	private GregorianCalendar dayLeft;
	private GregorianCalendar dayRight;
	private int mCurrentDayPosition = POSITION;
	private DBAdapter mAdapter;

	public DayPagerAdapter(Context context, long currentDay) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mAdapter = DBAdapter.getInstance();

		mCurrentDay = new GregorianCalendar();
		mCurrentDay.setTimeInMillis(currentDay);
		mCurrentDay.add(GregorianCalendar.DAY_OF_YEAR, -1);
		while (!mAdapter.isWorkDay(mCurrentDay)) {
			mCurrentDay.add(GregorianCalendar.DAY_OF_YEAR, -1);
		}
		// //
		mCurrentDayPosition--;
		dayLeft = new GregorianCalendar();
		dayLeft.setTimeInMillis(currentDay);

		dayRight = new GregorianCalendar();
		dayRight.setTimeInMillis(currentDay);

	}

	@Override
	public int getCount() {
		return 3 * LOOPS;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = null;
		GregorianCalendar needed;
		int shift;
		if (position - mCurrentDayPosition > 0) {
			shift = 1;
			needed = dayRight;
			mCurrentDayPosition++;
			dayLeft.setTimeInMillis(mCurrentDay.getTimeInMillis());
			mCurrentDay.setTimeInMillis(dayRight.getTimeInMillis());
		} else {
			shift = -1;
			needed = dayLeft;
			mCurrentDayPosition--;
			dayRight.setTimeInMillis(mCurrentDay.getTimeInMillis());
			mCurrentDay.setTimeInMillis(dayLeft.getTimeInMillis());
		}

		needed.add(GregorianCalendar.DAY_OF_YEAR, shift);
		while (!mAdapter.isWorkDay(needed)) {
			needed.add(GregorianCalendar.DAY_OF_YEAR, shift);
		}
		final long time = needed.getTimeInMillis();
		Day day = mAdapter.getDay(needed);
		// ////////////////////////////////////////////////////////////////////////////
		view = mInflater.inflate(R.layout.day_page, null);

		String[] daysOfWeek = mContext.getResources().getStringArray(
				R.array.days_of_week);
		String[] months = mContext.getResources().getStringArray(
				R.array.months_genitive);
		((TextView) view.findViewById(R.id.day_of_week))
				.setText(daysOfWeek[needed.get(GregorianCalendar.DAY_OF_WEEK) - 1]
						+ ", ");
		((TextView) view.findViewById(R.id.day_date)).setText(needed
				.get(GregorianCalendar.DAY_OF_MONTH) + " ");
		((TextView) view.findViewById(R.id.month_genitive))
				.setText(months[needed.get(GregorianCalendar.MONTH)]);

		final DayListAdapter adapter = new DayListAdapter(mContext,
				day.getPairs());

		ListView listView = (ListView) view.findViewById(R.id.listView1);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long arg) {
				Intent intent = new Intent(mContext, LessonActivity.class);
				intent.putExtra(LessonActivity.DAY, time);
				intent.putExtra(LessonActivity.PAIR, position);
				mContext.startActivity(intent);
			}
		});
		listView.setAdapter(adapter);

		/*
		 * LinearLayout alarmLayout = (LinearLayout)
		 * page.findViewById(R.id.alarm_layout); alarmLayout.setEnabled(true);
		 */
		TextView alarm = (TextView) view.findViewById(R.id.alarm_time);
		// ЗАГЛУШКА
		alarm.setText(position % 24 + ":00");
		//

		((ViewPager) container).addView(view, position % 3);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public void startUpdate(View container) {

	}

	@Override
	public void finishUpdate(View container) {

	}

}
