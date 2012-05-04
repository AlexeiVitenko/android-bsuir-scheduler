package by.bsuir.scheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.ShapeDrawable;
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

	public DayListAdapter(Context context, GregorianCalendar day,
			List<Pair> pairs) {
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
		holder.timeStart.setText(String.format("%2d:%02d", times[0], times[1]));
		holder.timeEnd.setText(String.format("%2d:%02d", times[2], times[3]));

		int green = Color.rgb(0, 190, 0);
		int gray = Color.rgb(100, 100, 100);
		Pair.PairStatus status = lesson.getStatus(); 
		switch (status.status) {
			case Pair.PAIR_STATUS_PAST:
			case Pair.PAIR_STATUS_CURRENT_DAY_PAST:
				holder.statusBar.setBackgroundColor(green);
				break;
			case Pair.PAIR_STATUS_CURRENT:
				float pct = ((float) status.progress)
						/ status.pair_length;
				/*holder.statusBar
						.setBackgroundColor(gradientColor(gray, green, pct));*/
				holder.statusBar.setBackgroundDrawable(getGradient(gray, green, pct));
				break;
			default:
				break;
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
		case 5:
			holder.classType.setImageResource(R.drawable.ic_physical_culture);
			break;
		case 6:
			holder.classType.setImageResource(R.drawable.ic_star);
			break;
		default:
			break;
		}

		holder.subject.setText(lesson.getLesson());
		holder.room.setText(lesson.getRoom());
		holder.teacher.setText(lesson.getTeacher());
	}

	private GradientDrawable getGradient(int startColor, int endColor ,float percent){
		GradientDrawable gradient = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{
			startColor,
			endColor
		});
		gradient.setGradientType(GradientDrawable.LINE);
		gradient.setGradientCenter(1f, percent);
		return gradient;
	}
	
	private int gradientColor(int startColor, int endColor, double percent) {
		double sR = Color.red(startColor);
		double sG = Color.green(startColor);
		double sB = Color.blue(startColor);
		double gR = Color.red(endColor) - sR;
		double gG = Color.green(endColor) - sG;
		double gB = Color.blue(endColor) - sB;
		return Color.rgb((int) (sR + gR * percent), (int) (sG + gG * percent),
				(int) (sB + gB * percent));
	}

	class ViewHolder {
		ImageView statusBar;
		TextView timeStart;
		TextView timeEnd;
		ImageView classType;
		TextView subject;
		TextView room;
		TextView teacher;
	}
}
