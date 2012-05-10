package by.bsuir.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import by.bsuir.scheduler.activity.SchedulerActivity;
import by.bsuir.scheduler.model.DBAdapter;
import by.bsuir.scheduler.model.DBAdapter.DayMatcherConditions;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;

public class GridCellAdapter extends BaseAdapter implements OnClickListener {
	public static final String DAY = "day";
	private final Activity context;
	private final List<GridCellData> list;
	private int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30,
			31 };
	private final int month, year;
	private int daysInMonth;
	private final int currentDayOfMonth;
	private Button gridcell;
	private int textViewResourceId;
	private DBAdapter mAdapter;

	public GridCellAdapter(Activity context, int textViewResourceId, int month,
			int year) {
		super();
		this.context = context;
		this.list = new ArrayList<GridCellData>();
		this.month = month;
		this.year = year;
		if (year % 4 == 0) {
			daysOfMonth[1] = 29;
		}
		daysInMonth = daysOfMonth[month];
		currentDayOfMonth = GregorianCalendar.getInstance().get(
				Calendar.DAY_OF_MONTH);
		this.textViewResourceId = textViewResourceId;
		mAdapter = DBAdapter.getInstance(context.getApplicationContext());

		printMonth(month, year);
	}

	public String getItem(int position) {
		return list.get(position).toString();
	}

	public int getCount() {
		return list.size();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			row = inflater.inflate(textViewResourceId, parent, false);
		}

		gridcell = (Button) row.findViewById(R.id.day_of_month_btn);
		final GridCellData data = list.get(position);
		gridcell.setText("" + data.getDay());
		gridcell.setTextColor(data.getColorText());

		if (data.getDay() == currentDayOfMonth
				&& data.getMonth() == GregorianCalendar.getInstance().get(
						GregorianCalendar.MONTH)) {
			gridcell.setTextColor(Color.BLUE);
			gridcell.setTextScaleX((float) 1.2);
		}

		if ((position + 1) % 7 != 0
				&& (mAdapter.dayMatcher(data.getDate()) == DayMatcherConditions.WORK_DAY
						|| mAdapter.dayMatcher(data.getDate()) == DayMatcherConditions.FIRST_DAY || mAdapter
						.dayMatcher(data.getDate()) == DayMatcherConditions.LAST_DAY)) {
//			gridcell.setShadowLayer(8f, 0, 0, Color.BLACK);
			gridcell.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra(DAY, (new GregorianCalendar(data.getYear(),
							data.getMonth(), data.getDay()).getTimeInMillis()));
					context.setResult(SchedulerActivity.RESULT_DAY, intent);
					context.finish();
				}
			});
		} else {
			gridcell.setBackgroundResource(R.layout.gradient_radial_2);
		}

		return row;
	}

	public void onClick(View view) {
	}

	private void printMonth(int mm, int yy) {
		int spacesBefore;
		int prevMonth = (12 + mm - 1) % 12;
		int nextMonth = (mm + 1) % 12;
		int prevYear = yy;
		int nextYear = yy;

		if (mm == 11) {
			nextYear = yy + 1;
		} else if (mm == 0) {
			prevYear = yy - 1;
		}

		GregorianCalendar cal = new GregorianCalendar(yy, mm, 1);
		// spacesBefore = cal.get(Calendar.DAY_OF_WEEK) - 1;
		spacesBefore = (7 + cal.get(Calendar.DAY_OF_WEEK) - 2) % 7;

		int daysInPrevMonth = daysOfMonth[prevMonth];
		if (prevMonth == 1) {
			if (prevYear % 4 == 0) {
				daysInPrevMonth = 29;
			} else {
				daysInPrevMonth = 28;
			}
		}

		for (int i = 0; i < spacesBefore; i++) {
			list.add(new GridCellData((daysInPrevMonth - spacesBefore + 1 + i),
					(prevMonth), prevYear, Color.LTGRAY));
		}
		for (int i = 1; i <= daysInMonth; i++) {
			list.add(new GridCellData(i, (mm), yy, Color.WHITE));
		}
		for (int i = 0; i < list.size() % 7; i++) {
			list.add(new GridCellData((i + 1), (nextMonth), nextYear,
					Color.LTGRAY));
		}
	}

	private class GridCellData {
		private int gYear;
		private int gMonth;
		private int gDay;
		private int gColorText;
		private GregorianCalendar gDate;

		public GridCellData(int day, int month, int year, int colorText) {
			gYear = year;
			gMonth = month;
			gDay = day;
			gColorText = colorText;
			gDate = new GregorianCalendar(year, month, day);
		}

		public int getYear() {
			return gYear;
		}

		public int getMonth() {
			return gMonth;
		}

		public int getDay() {
			return gDay;
		}

		public int getColorText() {
			return this.gColorText;
		}

		public GregorianCalendar getDate() {
			return gDate;
		}

		@Override
		public String toString() {
			return "" + gDay + "-" + gMonth + "-" + gYear + "-" + gColorText;
		}
	}

}
