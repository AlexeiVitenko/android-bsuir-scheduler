package by.bsuir.scheduler;

import java.util.ArrayList;
import java.util.List;

import by.bsuir.scheduler.R;
import by.bsuir.scheduler.model.Pair;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DayListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Pair> mPairs;

	public DayListAdapter(Context context, List<Pair> pairs) {
		inflater = LayoutInflater.from(context);
		mPairs = pairs;
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

		// ЗАГЛУШКА
		holder.statusBar.setBackgroundColor(Color.YELLOW);
		//
		int [] times = lesson.getTime();
		holder.timeStart.setText(""+times[0]+":"+times[1]);
		holder.timeEnd.setText("- "+times[2]+":"+times[3]);
		
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
			holder.classType.setImageResource(R.drawable.ic_control);
			break;
		default:
			holder.classType.setImageResource(R.drawable.physical_culture);
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
