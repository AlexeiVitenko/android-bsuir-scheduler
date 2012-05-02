package by.bsuir.scheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import by.bsuir.scheduler.model.Pair;

public class DayListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Pair> mPairs;
	private GregorianCalendar currentDay;

	public DayListAdapter(Context context, GregorianCalendar day, List<Pair> pairs) {
		inflater = LayoutInflater.from(context);
		mPairs = pairs;
		currentDay = day;
	}

	public void setList(List<Pair> mPairs) {
		this.mPairs = mPairs;
	}

	public int getCount() {
		return mPairs.size();
	}

	public Object getItem(int position) {
		return mPairs.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = newView(position, parent);
		}

		bindView(position, convertView);

		return convertView;
	}

	private View newView(int position, ViewGroup parent) {
		View view = inflater.inflate(R.layout.day_list_item, parent, false);
		ViewHolder holder = getViewHolder(view);
		view.setTag(holder);
		return view;
	}

	private ViewHolder getViewHolder(View view) {
		ViewHolder holder = new ViewHolder();
		holder.statusBar = (ImageView) view.findViewById(R.id.status_bar);
		holder.timeStart = (TextView) view.findViewById(R.id.time_start);
		holder.timeEnd = (TextView) view.findViewById(R.id.time_end);
		holder.classType = (ImageView) view.findViewById(R.id.class_type);
		holder.subject = (TextView) view.findViewById(R.id.subject);
		holder.room = (TextView) view.findViewById(R.id.room);
		holder.teacher = (TextView) view.findViewById(R.id.teacher);
		return holder;
	}

	private void bindView(int position, View view) {
		ViewHolder holder = (ViewHolder) view.getTag();

		Pair lesson = mPairs.get(position);

		int[] times = lesson.getTime();
		holder.timeStart.setText("" + times[0] + ":" + times[1]);
		holder.timeEnd.setText("- " + times[2] + ":" + times[3]);

		Calendar time = GregorianCalendar.getInstance();
		int nHour = time.get(GregorianCalendar.HOUR_OF_DAY);
		int nMinute = time.get(GregorianCalendar.MINUTE);
		int nDay = time.get(GregorianCalendar.DAY_OF_MONTH);
		int nMonth = time.get(GregorianCalendar.MONTH);
		int cDay = currentDay.get(GregorianCalendar.DAY_OF_MONTH);
		int cMomth = currentDay.get(GregorianCalendar.MONTH);
		
		if (nDay == cDay && nMonth == cMomth) {
		if (nHour > times[0] || (nHour == times[0] && nMinute > times[1])) {
			if (nHour < times[2] || (nHour == times[2] && nMinute < times[3])) {
				double pct = (double) (60 * (nHour - times[0]) + (nMinute - times[1])) / 90;
				double sR = Color.red(Color.GRAY);
				double sG = Color.green(Color.GRAY);
				double sB = Color.blue(Color.GRAY);
				double gR = Color.red(Color.GREEN) - sR;
				double gG = Color.green(Color.GREEN) - sG;
				double gB = Color.blue(Color.GREEN) - sB;
				Log.i("DayListAdapter", "" + pct);
				Log.i("DayListAdapter", "" + (int) (sR + gR * pct) + " "
						+ (int) (sG + gG * pct) + " " + (int) (sB + gB * pct));
				holder.statusBar.setBackgroundColor(Color.rgb((int) (sR + gR
						* pct), (int) (sG + gG * pct), (int) (sB + gB * pct)));
			} else {
				holder.statusBar.setBackgroundColor(Color.GREEN);
			}
		} else {
			holder.statusBar.setBackgroundColor(Color.GRAY);
		}
		}

		switch (lesson.getType()) {
		case 1:
			holder.classType.setImageResource(R.drawable.ic_lecture);
			break;
		case 2:
			holder.classType.setImageResource(R.drawable.ic_practice);
			break;
		case 3:
			holder.classType.setImageResource(R.drawable.ic_laboratory);
			break;
		case 4:
			holder.classType.setImageResource(R.drawable.ic_course);
			break;
		case -1:
			holder.classType.setImageResource(R.drawable.ic_physical_culture);
			break;
		default:
			break;
		}

		holder.subject.setText(lesson.getLesson());
		holder.room.setText(lesson.getRoom());
		holder.teacher.setText(lesson.getTeacher());
	}

	class ViewHolder {
		public ImageView statusBar;
		public TextView timeStart;
		public TextView timeEnd;
		public ImageView classType;
		public TextView subject;
		public TextView room;
		public TextView teacher;
	}
}
