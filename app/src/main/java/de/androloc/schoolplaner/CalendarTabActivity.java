package de.androloc.schoolplaner;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class CalendarTabActivity extends TabActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar_tab);

	    String yearTitle = getTitle() + " " + ClassSettings.getActiveYearShow();
	    setTitle(yearTitle);
	    
	    Resources res = getResources(); 	// Resources-Objekt für die images
	    TabHost tabHost = getTabHost();  	// Tab-Host
	    TabHost.TabSpec spec;  				// Tab-Spezifikationen für die einzelnen Tabs
	    Intent intent;  					// Intents der einzelnen Tabs

	    //Tab für Tages-Anzeige
	    try {
		    intent = new Intent().setClass(this, CalendarDayActivity.class);
		    spec = tabHost.newTabSpec("day");
		    spec.setIndicator(getString(R.string.tab_calendar_day),res.getDrawable(R.drawable.calendar_day));
		    spec.setContent(intent);
		    tabHost.addTab(spec);
		} catch (Exception e) {
		}

	    //Tab für Wochen-Anzeige
	    try {
		    intent = new Intent().setClass(this, CalendarWeekActivity.class);
		    spec = tabHost.newTabSpec("week");
		    spec.setIndicator(getString(R.string.tab_calendar_week),res.getDrawable(R.drawable.calendar_week));
		    spec.setContent(intent);
		    tabHost.addTab(spec);
		} catch (Exception e) {
		}
	
	    //Tab für Monats-Anzeige
	    try {
		    intent = new Intent().setClass(this, CalendarMonthActivity.class);
		    spec = tabHost.newTabSpec("month");
		    spec.setIndicator(getString(R.string.tab_calendar_month),res.getDrawable(R.drawable.calendar_month));
		    spec.setContent(intent);
		    tabHost.addTab(spec);
		} catch (Exception e) {
		}
	}
}
