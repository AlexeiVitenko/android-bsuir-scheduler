package by.bsuir.scheduler.activity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import by.bsuir.scheduler.R;
import by.bsuir.scheduler.model.DBAdapter;
import by.bsuir.scheduler.model.Pair;

public class LessonActivity extends Activity {
	public static final String DAY="day";
	public static final String PAIR="pair";
	private final static int DIALOG = 1;
	private Pair mLesson;
	private String[] daysOfWeek;
	private GregorianCalendar mTime;
	private DBAdapter mAdapter;
	private TextView mNoteText;
	//private int lessonID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lesson);
		daysOfWeek = this.getResources().getStringArray(
					R.array.days_of_week);
		String [] lessonType = this.getResources().getStringArray(R.array.lesson_types);
				// /
		// /типа запрос в БД
		// /
		mTime = new GregorianCalendar(Locale.getDefault());
		mTime.setTimeInMillis(getIntent().getLongExtra(DAY, -1));
		mLesson = ( mAdapter = DBAdapter.getInstance(getApplicationContext())).getPair(mTime); //, getIntent().getIntExtra(PAIR, -1));
		((TextView)findViewById(R.id.day_of_week)).setText(""+daysOfWeek[mTime.get(GregorianCalendar.DAY_OF_WEEK)-1]+", "+mTime.get(GregorianCalendar.DAY_OF_MONTH)+
				" "+getResources().getStringArray(R.array.months_genitive)[mTime.get(Calendar.MONTH)]);
		TextView subject = (TextView) findViewById(R.id.lesson_subject);
		subject.setText(mLesson.getLesson());
		TextView teacher = (TextView) findViewById(R.id.lesson_teacher);
		teacher.setText(mLesson.getTeacher());
		((TextView)findViewById(R.id.lesson_type)).setText(lessonType[mLesson.getType()]);
		TextView time = (TextView) findViewById(R.id.lesson_time);
		time.setText(String.format("%2d:%02d-%2d:%02d", mLesson.getTime()[0],mLesson.getTime()[1],mLesson.getTime()[2],mLesson.getTime()[3]));
		TextView room = (TextView) findViewById(R.id.lesson_room);
		room.setText("ауд. " + mLesson.getRoom());
		mNoteText = (TextView) findViewById(R.id.lesson_note);
		mNoteText.setText(mLesson.getNote());
		mNoteText.setOnLongClickListener(new OnLongClickListener() {
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

		final EditText note = (EditText) view.findViewById(R.id.dialog_note);
		note.setText(mLesson.getNote());

		builder.setPositiveButton(R.string.dialog_save, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mLesson.setNote(note.getText().toString());
				mNoteText.setText(note.getText().toString());
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
						Log.i("sdfsdf", "sdfsdfs");
						note.setText("");
					}
				});

		return dialog;
	}

}
