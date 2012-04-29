package by.bsuir.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import by.bsuir.scheduler.activity.SchedulerActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;

public class GridCellAdapter extends BaseAdapter implements OnClickListener {
	public static final String DAY = "day";
	private final Activity context;
	private final List<String> list;
	private int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30,
			31 };
	private final int month, year;
	private int daysInMonth;
	private final int currentDayOfMonth;
	private Button gridcell;

	public GridCellAdapter(Activity context, int textViewResourceId, int month,
			int year) {
		super();
		this.context = context;
		this.list = new ArrayList<String>();
		this.month = month;
		this.year = year;
		if (year % 4 == 0) {
			daysOfMonth[1] = 29;
		}
		daysInMonth = daysOfMonth[month];
		currentDayOfMonth = GregorianCalendar.getInstance().get(
				Calendar.DAY_OF_MONTH);

		printMonth(month, year);
	}

	public String getItem(int position) {
		return list.get(position);
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
			row = inflater.inflate(R.layout.day_of_month, parent, false);
		}

		gridcell = (Button) row.findViewById(R.id.day_of_month_btn);
		// вставить проверку на учебный ли день...
		gridcell.setOnClickListener(this);

		String[] day_color = list.get(position).split("-");
		gridcell.setText(day_color[0]);
		gridcell.setTag(day_color[0] + "-" + day_color[2] + "-" + day_color[3]);

		if (day_color[1].equals("GREY")) {
			gridcell.setTextColor(Color.LTGRAY);
		}
		if (day_color[1].equals("WHITE")) {
			gridcell.setTextColor(Color.WHITE);
			Calendar today = GregorianCalendar.getInstance();
			if (Integer.valueOf(day_color[0]) == currentDayOfMonth
					&& today.get(Calendar.MONTH) == month
					&& today.get(Calendar.YEAR) == year) {
				gridcell.setTextColor(Color.BLUE);
				gridcell.setTextScaleX((float) 1.2);
			}
		}

		return row;
	}

	public void onClick(View view) {
		Intent intent = new Intent();
		String[] str = ((String) view.getTag()).split("-");
		intent.putExtra(DAY, (new GregorianCalendar(Integer.parseInt(str[2]),
				Integer.parseInt(str[1])-1, Integer.parseInt(str[0]))
				.getTimeInMillis()));
		context.setResult(SchedulerActivity.RESULT_DAY, intent);
		context.finish();
	}

	private void printMonth(int mm, int yy) {
		int spacesBefore;
		int prevMonth = (12 + mm - 1) % 12;
		int nextMonth = (mm + 1) % 12;
		int prevYear = 0;
		int nextYear = 0;

		if (mm == 11) {
			prevYear = yy;
			nextYear = yy + 1;
		} else if (mm == 0) {
			prevYear = yy - 1;
			nextYear = yy;
		} else {
			nextYear = yy;
			prevYear = yy;
		}

		GregorianCalendar cal = new GregorianCalendar(yy, mm, 1);
		// почему-то календарь врет. у него все на +1 к дню недели
		spacesBefore = (7 + cal.get(Calendar.DAY_OF_WEEK) - 2) % 7;

		int daysInPrevMonth = daysOfMonth[prevMonth];

		for (int i = 0; i < spacesBefore; i++) {
			list.add("" + (daysInPrevMonth - spacesBefore + 1 + i) + "-GREY-"
					+ (prevMonth + 1) + "-" + prevYear);
		}

		for (int i = 1; i <= daysInMonth; i++) {
			list.add("" + i + "-WHITE-" + (mm + 1) + "-" + yy);
		}

		for (int i = 0; i < list.size() % 7; i++) {
			list.add("" + (i + 1) + "-GREY-" + (nextMonth + 1) + "-" + nextYear);
		}
	}

}
