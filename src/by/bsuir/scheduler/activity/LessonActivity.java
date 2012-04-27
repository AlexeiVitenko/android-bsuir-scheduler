package by.bsuir.scheduler.activity;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import by.bsuir.scheduler.DayPagerAdapter;

import by.bsuir.scheduler.R;
import by.bsuir.scheduler.model.DBAdapter;
import by.bsuir.scheduler.model.Pair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LessonActivity extends Activity {
	public static final String DAY = "day";
	public static final String PAIR = "pair";
	private final static int DIALOG = 1;
	private int lessonID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lesson);
		// /
		// /типа запрос в БД
		// /
		GregorianCalendar day = new GregorianCalendar();
		day.setTimeInMillis(getIntent().getLongExtra(DAY, -1));
		Pair lesson = DBAdapter.getInstance().getPair(day,
				getIntent().getIntExtra(PAIR, -1));
		
		String[] daysOfWeek = getResources().getStringArray(
				R.array.days_of_week);
		String[] months = getResources()
				.getStringArray(R.array.months_genitive);
		((TextView) findViewById(R.id.day_of_week)).setText(daysOfWeek[day
				.get(GregorianCalendar.DAY_OF_WEEK) - 1] + ", ");
		((TextView) findViewById(R.id.day_date)).setText(day
				.get(GregorianCalendar.DAY_OF_MONTH) + " ");
		((TextView) findViewById(R.id.month_genitive)).setText(
				months[day.get(GregorianCalendar.MONTH)]);

		((TextView) findViewById(R.id.lesson_subject)).setText(lesson.getLesson());
		((TextView) findViewById(R.id.lesson_teacher)).setText(lesson.getTeacher());
		((TextView) findViewById(R.id.lesson_type)).setText(lesson.getStringType());
		TextView time = (TextView) findViewById(R.id.lesson_time);
		time.setText(String.format("%d:%d-%d:%d", lesson.getTime()[0],
				lesson.getTime()[1], lesson.getTime()[2], lesson.getTime()[3]));
		((TextView) findViewById(R.id.lesson_room)).setText("ауд. " + lesson.getRoom());
		TextView note = (TextView) findViewById(R.id.lesson_note);
		note.setText(lesson.getNote());
		note.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				showDialog(DIALOG);
				return true;
			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG:
			return createNoteDialog(this);

		default:
			return super.onCreateDialog(id);
		}

	}

	private Dialog createNoteDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.note);
		View view = (LinearLayout) getLayoutInflater().inflate(
				R.layout.note_dialog, null);
		builder.setView(view);

		String noteText = "текст заметки";
		// /
		// /обращаемся в БД за заметкой
		// /
		final EditText note = (EditText) view.findViewById(R.id.dialog_note);
		note.setText(noteText);

		builder.setPositiveButton(R.string.dialog_save, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// типа сохраняем в БД
			}
		});

		DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		};

		builder.setNeutralButton(R.string.dialog_clear, clickListener);
		builder.setNegativeButton(R.string.dialog_cancel, clickListener);

		Dialog dialog = builder.create();
		dialog.show();

		((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL)
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						note.setText("");
					}
				});

		return dialog;
	}

}
