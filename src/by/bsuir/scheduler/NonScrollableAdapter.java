package by.bsuir.scheduler;

import java.util.Calendar;
import java.util.List;

import by.bsuir.scheduler.activity.ConfiguratorActivity;

import android.support.v4.view.LimitedViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

public class NonScrollableAdapter extends PagerAdapter {
	private List<View> mViews;
	
	public NonScrollableAdapter(List<View> views){
		mViews = views;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View v = mViews.get(position);
		((ViewPager)container).addView(v,position);
		return v;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}
	
	@Override
	public int getCount() {
		return mViews.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}
	
	public void copleteInput(ConfiguratorActivity activity){
		String group = ((EditText)mViews.get(0).findViewById(R.id.config_0_group)).getText().toString();
		int subGroup = ((Spinner)mViews.get(0).findViewById(R.id.config_0_spinner)).getSelectedItemPosition();
		
		String weeks = ((EditText)mViews.get(1).findViewById(R.id.config_1_weeks)).getText().toString();
		Calendar c = Calendar.getInstance();
		DatePicker picker = ((DatePicker)mViews.get(1).findViewById(R.id.config_1_date));
		c.set(Calendar.YEAR, picker.getYear());
		c.set(Calendar.MONTH, picker.getMonth());
		c.set(Calendar.DAY_OF_MONTH, picker.getDayOfMonth());
		CheckBox checkBox = (CheckBox)mViews.get(2).findViewById(R.id.iAmCrazy);
		if (group.length()>0 && weeks.length()>0 && checkBox.isChecked()) {
			activity.setResult(group, subGroup, weeks, c.getTimeInMillis());
		}
	}
}
