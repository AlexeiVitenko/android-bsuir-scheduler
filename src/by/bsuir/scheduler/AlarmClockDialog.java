package by.bsuir.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmClockDialog extends Dialog {
	
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
			"HH:mm");
	
	private Context context;

	public AlarmClockDialog(Context context, Intent intent) {
		super(context);
		this.context = context;
		
		Toast.makeText(context, "" + TIME_FORMAT.format(new Date(intent.getLongExtra(AlarmClockReceiver.ALARM_TIME, 0))),
				Toast.LENGTH_SHORT);
	}

}
