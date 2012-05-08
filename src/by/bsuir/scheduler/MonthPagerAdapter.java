package by.bsuir.scheduler;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import by.bsuir.scheduler.R;
import by.bsuir.scheduler.model.DBAdapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

public class MonthPagerAdapter extends PagerAdapter {

	private Activity context;
	private LayoutInflater inflater;
	private List<View> pages;
	private GregorianCalendar calendar;
//	private int numberOfWeeks;
//	private int monthOfStart2sem;
	private int firstMonth;
	private int lastMonth;
	private int year;

	public MonthPagerAdapter(Activity context) {
		DBAdapter dbAdapter = DBAdapter.getInstance(context.getApplicationContext());
		firstMonth = dbAdapter.getFirstMonth();
		lastMonth = dbAdapter.getLastMonth();
		year = dbAdapter.getYear();
//		this.numberOfWeeks = numberOfWeeks;
//		this.dayOfStart2sem = dayOfStart2sem;
//		this.monthOfStart2sem = monthOfStart2sem;
		this.context = context;
		inflater = LayoutInflater.from(context);
		calendar = new GregorianCalendar(Locale.getDefault());
	}

	@Override
	public int getCount() {
		/*if (numberOfWeeks > 17) {
			return 5;
		} else {
			return 4;
		}*/	
		return lastMonth - firstMonth + 1  ;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		if (pages == null) {
//			pages = generatePages(container,
//					calendar.get(GregorianCalendar.MONTH),
//					calendar.get(GregorianCalendar.YEAR));
			pages = generatePages(container);
		}

		//View view = pages.get(position);
		/*((ViewPager) container).addView(view, ((ViewPager) container)
				.getChildCount() > position ? position
				: ((ViewPager) container).getChildCount());*/
		//((ViewPager) container).addView(view, position);
		return pages.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
	//	((ViewPager) container).removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	public int getCurrentItem(int currentMonth) {
		int currentItem = 0;
//		int sep = GregorianCalendar.SEPTEMBER;
//		int dec = GregorianCalendar.DECEMBER;
//		int may = GregorianCalendar.MAY;
//		int currentMonth = calendar.get(GregorianCalendar.MONTH);
		currentItem = currentMonth - firstMonth;
//		if (sep <= month && dec >= month) {
//			currentItem = month - sep;
//
//		} else if (monthOfStart2sem <= month && may >= month) {
//			currentItem = month - monthOfStart2sem;
//		}
		return currentItem;
	}

	private List<View> generatePages(ViewGroup container) {
		List<View> list = new ArrayList<View>();
//		int sep = GregorianCalendar.SEPTEMBER;
//		int dec = GregorianCalendar.DECEMBER;
//		int may = GregorianCalendar.MAY;
//		int startMonth = 0;
//		int endMonth = 0;
//		if (sep <= month && dec >= month) {
//			startMonth = sep;
//			endMonth = dec;
//
//		} else if (monthOfStart2sem <= month && may >= month) {
//			startMonth = monthOfStart2sem;
//			endMonth = may;
//		}

		if ((lastMonth - firstMonth) > 0) {

			String[] months = context.getResources().getStringArray(
					R.array.months);
			for (int i = firstMonth; i <= lastMonth; i++) {
				GridCellAdapter gridCellAdapter = new GridCellAdapter(context,
						R.layout.day_of_month, i, year);
				gridCellAdapter.notifyDataSetChanged();

				View view = inflater.inflate(R.layout.month_page, container,
						false);
				((TextView) view.findViewById(R.id.month)).setText(months[i]
						+ ", ");
				((TextView) view.findViewById(R.id.year)).setText("" + year);
				((GridView) view.findViewById(R.id.calendar))
						.setAdapter(gridCellAdapter);

				int[] daysOfWeekID = { R.id.Mo, R.id.Tu, R.id.We, R.id.Th,
						R.id.Fr, R.id.Sa, R.id.Su };
				String[] daysOfWeek = context.getResources().getStringArray(
						R.array.days_of_week_abb);
				for (int j = 0; j < 7; j++) {
					((TextView) view.findViewById(daysOfWeekID[j]))
							.setText(daysOfWeek[j]);
				}

				list.add(view);
				((ViewPager) container).addView(view, i - firstMonth);
			}
		}

		return list;
	}

}
