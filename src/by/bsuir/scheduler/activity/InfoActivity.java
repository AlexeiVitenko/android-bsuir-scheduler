package by.bsuir.scheduler.activity;

import by.bsuir.scheduler.R;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class InfoActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.about);
		
		try {
			Preference pref = findPreference("vName");
			pref.setSummary(getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
			pref = findPreference("vCode");
			pref.setSummary(""+getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
