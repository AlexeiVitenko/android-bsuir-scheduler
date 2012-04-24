package by.bsuir.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import by.bsuir.scheduler.activity.SchedulerActivity;

import by.bsuir.scheduler.R;
import by.bsuir.scheduler.R.layout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MonthPagerAdapter extends PagerAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<View> pages;
	private GregorianCalendar calendar;
	private int numberOfWeeks;
	private int dayOfStart2sem;
	private int monthOfStart2sem;
	private int year;
	private int currentItem = 0;

	public MonthPagerAdapter(int numberOfWeeks, int dayOfStart2sem, int monthOfStart2sem, Context context) {
		this.numberOfWeeks = numberOfWeeks;
		this.dayOfStart2sem = dayOfStart2sem;
		this.monthOfStart2sem = monthOfStart2sem;
		this.context = context;
		inflater = LayoutInflater.from(context);
		calendar = new GregorianCalendar(Locale.getDefault());
		year = calendar.get(GregorianCalendar.YEAR);
		pages = generatePages(calendar.get(GregorianCalendar.MONTH), year);
	}

	@Override
	public int getCount() {
		if (numberOfWeeks > 17) {
			return 5;
		} else {
			return 4;
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = pages.get(position);
		((ViewPager) container).addView(view, ((ViewPager) container)
				.getChildCount() > position ? position
				: ((ViewPager) container).getChildCount());
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	public int getCurrentItem() {
		return currentItem;
	}

	private List<View> generatePages(int month, int year) {
		List<View> list = new ArrayList<View>();
		int sep = GregorianCalendar.SEPTEMBER;
		int dec = GregorianCalendar.DECEMBER;
		int may = GregorianCalendar.MAY;
		int startMonth = 0;
		int endMonth = 0;
		if (sep <= month && dec >= month) {
			currentItem = month - sep;
			startMonth = sep;
			endMonth = dec;
			
		} else if (monthOfStart2sem <= month && may >= month) {
			currentItem = month - monthOfStart2sem;
			startMonth = monthOfStart2sem;
			endMonth = may;
		}
		
		if ((endMonth - startMonth) > 0) {
			for (int i = startMonth; i <= endMonth; i++) {
				GridCellAdapter gridCellAdapter = new GridCellAdapter(context, R.layout.day_of_month, i, year);
				gridCellAdapter.notifyDataSetChanged();
				View view = new View(context);
				view.setId(R.layout.month_page);
				((TextView) view.findViewById(R.id.month)).setText(gridCellAdapter.getMonth(i));
				((TextView) view.findViewById(R.id.year)).setText("" + year);
				((GridView) view.findViewById(R.id.calendar)).setAdapter(gridCellAdapter);
				list.add(view);
			}
		}
		
		return list;
	}

	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private final Context _context;
		private final List<String> list;
		private final String[] weekdays = new String[] { "Sun", "Mon", "Tue",
				"Wed", "Thu", "Fri", "Sat" };
		private final String[] months = { "January", "February", "March",
				"April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
				31, 30, 31 };
		private final int month, year;
		private int daysInMonth, prevMonthDays;
		private final int currentDayOfMonth;
		private Button gridcell;

		// Days in Current Month
		public GridCellAdapter(Context context, int textViewResourceId,
				int month, int year) {
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			this.month = month;
			this.year = year;

			Calendar calendar = Calendar.getInstance();
			currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

			printMonth(month, year);
		}

		public String getItem(int position) {
			return list.get(position);
		}

		public int getCount() {
			return list.size();
		}

		private void printMonth(int mm, int yy) {
			// The number of days to leave blank at
			// the start of this month.
			int trailingSpaces = 0;
			int leadSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			GregorianCalendar cal = new GregorianCalendar(yy, mm,
					currentDayOfMonth);

			// Days in Current Month
			daysInMonth = daysOfMonth[mm];
			int currentMonth = mm;
			if (currentMonth == 11) {
				prevMonth = 10;
				daysInPrevMonth = daysOfMonth[prevMonth];
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = daysOfMonth[prevMonth];
				nextMonth = 1;
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = daysOfMonth[prevMonth];
			}

			// Compute how much to leave before before the first day of the
			// month.
			// getDay() returns 0 for Sunday.
			trailingSpaces = cal.get(Calendar.DAY_OF_WEEK) - 1;

			if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
				++daysInMonth;
			}

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
				list.add(String.valueOf((daysInPrevMonth - trailingSpaces + 1)
						+ i)
						+ "-GREY" + "-" + months[prevMonth] + "-" + prevYear);
			}

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				list.add(String.valueOf(i) + "-WHITE" + "-" + months[mm] + "-"
						+ yy);
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				list.add(String.valueOf(i + 1) + "-GREY" + "-"
						+ months[nextMonth] + "-" + nextYear);
			}
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				// ROW INFLATION
				LayoutInflater inflater = (LayoutInflater) _context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.day_of_month, parent, false);
			}

			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.day_of_month);
			gridcell.setOnClickListener(this);

			// ACCOUNT FOR SPACING
			String[] day_color = list.get(position).split("-");
			gridcell.setText(day_color[0]);
			gridcell.setTag(day_color[0] + "-" + day_color[2] + "-"
					+ day_color[3]);

			if (day_color[1].equals("GREY")) {
				gridcell.setTextColor(Color.LTGRAY);
			}
			if (day_color[1].equals("WHITE")) {
				gridcell.setTextColor(Color.WHITE);
			}
			if (position == currentDayOfMonth) {
				gridcell.setTextColor(Color.BLUE);
			}

			return row;
		}
		
		public String getMonth(int i) {
			return months[i];
		}

		public void onClick(View view) {
			String date_month_year = (String) view.getTag();
			Intent intent = new Intent(_context, SchedulerActivity.class);
			intent.putExtra("day", date_month_year);
			_context.startActivity(intent);
		}
	}

}
