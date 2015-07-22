package de.androloc.schoolplaner;

import java.util.List;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class CalendarMonthActivity extends Activity {

	private SharedPreferences calendar_Preferences;				//Kalender einstellungen
	private Boolean cal_saturday;								//Samstagsunterricht Ja/Nein 

	ClassCalendar cal;											//Kalender-Objekt
	ClassCalendarDay day;										//Tag-Objekt
	ClassHomework hw;											//Hausaufgaben-Objekt
	ClassNotes note;											//Notizen-Objekt
	ClassTests test;											//Tests-Objekt
	ImageButton button_Next;
	ImageButton button_Previous;
	ImageButton button_Home;
	ImageView button_menu[] = new ImageView[4];
	TextView tv_monthyear;
	TextView tv_days[];

	/* Kalenderwerte */
	private int activeMonth;
	private int activeYear;
	private int thisMonth;
	private int thisYear;
	private String[] days;										//Tage (Text)
	private String[] months;									//Monate (Text)
	
	/* Objekte für Datenbank und Tabellen */
	private ClassCalendarDay calDays[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar_month);

	    /*Kalender-Einstellingen initialisieren und Samstag-Unterricht prüfen
	      ------------------------------------------------------------------- */
	 	calendar_Preferences = PreferenceManager.getDefaultSharedPreferences(this);
	 	cal_saturday = calendar_Preferences.getBoolean("cal_saturday", false);

	    /* Benütigte Objekte für die Kalenderdarstellung und
	     * die Kalendernavigation
	     --------------------------------------------------- */
	    cal = new ClassCalendar(this);
	    hw = new ClassHomework(this);
	    note = new ClassNotes(this);
	    test = new ClassTests(this);
	    calDays = new ClassCalendarDay[37];
	
	    /* tage und Monate als Text-Arrays */
	 	Resources res = getResources();
	 	days = res.getStringArray(R.array.all_days);
	 	months = res.getStringArray(R.array.month);

	    /* Topic-Anzeige, Tag und datum */
		tv_monthyear = (TextView)findViewById(R.id.tv_cal_monthyear); 

		/* die einzelnen Tage */
		tv_days = new TextView[37];
		String idStringDay;
		int idTag;
		for (int i=0;i<37;i++) {
			try {
				idStringDay = "month_day_" + Integer.toString(i+1);
				idTag = R.id.class.getField(idStringDay).getInt(null);
				tv_days[i] = (TextView)findViewById(idTag);
				tv_days[i].setClickable(true);
				tv_days[i].setOnClickListener(dayClickListener);
				tv_days[i].setTag(i);
			} catch (Exception e) {
			}
		}
		
	    /* Next, Previous und Home - Buttons */
	    button_Next = (ImageButton)findViewById(R.id.button_navi_next);
	    button_Previous = (ImageButton)findViewById(R.id.button_navi_previous);
	    button_Home = (ImageButton)findViewById(R.id.button_navi_home);
	    
	    /*Mini-Menü-Buttons zum Umschalten der verschiedenen Views*/
	    button_menu[0] = (ImageView)findViewById(R.id.menu_month_s);
	    button_menu[1] = (ImageView)findViewById(R.id.menu_month_h);
	    button_menu[2] = (ImageView)findViewById(R.id.menu_month_t);
	    button_menu[3] = (ImageView)findViewById(R.id.menu_month_n);

	    /* Aktuellen Monat und aktuelles Jahr ermitteln */
	    if (ClassCalendar.getActiveDayID() > 0) {
		    day = cal.GetDayByID(ClassCalendar.getActiveDayID());
	    } else {
		    day = ClassCalendar.getToday();
	    }
    	if (day == null) {
    		finish();
    	}
	    activeMonth = day.getMonth();
	    activeYear = day.getYear();
    	thisMonth = ClassCalendar.getToday().getMonth();
    	thisYear = ClassCalendar.getToday().getYear();
	    showMonthData();

	    /* Listener für die navigations-Buttons */
	    button_Next.setOnClickListener(buttonNextListener);
	    button_Previous.setOnClickListener(buttonPreviousListener);
	    button_Home.setOnClickListener(buttonHomeListener);

	    /* Listener für die Mini-Menü-Buttons */
	    button_menu[0].setOnClickListener(buttonPlanListener);
	    button_menu[1].setOnClickListener(buttonHausaufgabenListener);
	    button_menu[2].setOnClickListener(buttonTestsListener);
	    button_menu[3].setOnClickListener(buttonNotizenListener);

	    /* Aktuellen View aktivieren */
	    setActiveView(ClassCalendar.getActiveView());

	}

	/* Falls Activity neu aufgerufen wird und ein bestimmter tag angezeigt werden soll */
	@Override
	protected void onResume() {
		super.onResume();
		showMonthData();
	};

	
	/* Click auf einen Tag im Kalender
	 * ------------------------------- */
	OnClickListener dayClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (ClassCalendar.getActiveView()) {
			case 0:
				//Zur Stundenplan Tagesansicht wechseln
				ClassCalendar.setActiveDayID(calDays[(Integer)v.getTag()].getId());
				((TabHost)getParent().findViewById(android.R.id.tabhost)).setCurrentTab(0);
				break;
			case 1:
				//zur Hausaufgaben-Ansicht wechseln
				ClassCalendarDay hwDay = calDays[(Integer)v.getTag()];
				daySelectedDialog(hwDay);
				break;
			case 2:
				//zur Test-Ansicht wechseln
				ClassCalendarDay testDay = calDays[(Integer)v.getTag()];
				daySelectedDialog(testDay);
				break;
			case 3:
				//zur Notizen-Ansicht wechseln
				ClassCalendarDay noteDay = calDays[(Integer)v.getTag()];
				daySelectedDialog(noteDay);
				break;
			}
		}
	};
	
	/* Navigations-Buttons-Listener 
	 * ---------------------------- */
	//Heute
	OnClickListener buttonHomeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			activeMonth = thisMonth;
			activeYear = thisYear;
			showMonthData();
		}
	};
	//Nächster Monat
	OnClickListener buttonNextListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			activeMonth += 1;
			if (activeMonth == 13) {
				activeYear += 1;
				activeMonth = 1;
			}
			showMonthData();
		}
	};
	//Vorheriger Tag
	OnClickListener buttonPreviousListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			activeMonth -= 1;
			if (activeMonth == 0) {
				activeYear -= 1;
				activeMonth = 12;
			}
			showMonthData();
		}
	};
	/* Mini-Menü-Listener 
	 * ------------------ */
	OnClickListener buttonPlanListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ClassCalendar.getActiveView() != 0) {
				ClassCalendar.setActiveView(0);
				setActiveView(ClassCalendar.getActiveView());
				showMonthData();
			}
		}
	};
	OnClickListener buttonHausaufgabenListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ClassCalendar.getActiveView() != 1) {
				ClassCalendar.setActiveView(1);
				setActiveView(ClassCalendar.getActiveView());
				showMonthData();
			}
		}
	};
	OnClickListener buttonTestsListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ClassCalendar.getActiveView() != 2) {
				ClassCalendar.setActiveView(2);
				setActiveView(ClassCalendar.getActiveView());
				showMonthData();
			}
		}
	};
	OnClickListener buttonNotizenListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ClassCalendar.getActiveView() != 3) {
				ClassCalendar.setActiveView(3);
				setActiveView(ClassCalendar.getActiveView());
				showMonthData();
			}
		}
	};

	/* den aktuellen Monat anzeigen */
	private void showMonthData() {
		
		if (activeMonth == 0) {
			finish();
		}

		/* Monat und Jahr */
		String monthyear = months[activeMonth-1] + " " + Integer.toString(activeYear); 
		tv_monthyear.setText(monthyear);

		/* Navigations-Buttons deaktivieren/deaktivieren */
		if (activeYear == ClassCalendar.getLastYear() && activeMonth == ClassCalendar.getLastMonth()) {
			button_Next.setEnabled(false);
		} else {
			button_Next.setEnabled(true);
		}
		if (activeYear == ClassCalendar.getFirstYear() && activeMonth == ClassCalendar.getFirstMonth()) {
			button_Previous.setEnabled(false);
		} else {
			button_Previous.setEnabled(true);
		} 
		if (activeYear == thisYear && activeMonth == thisMonth) {
			button_Home.setEnabled(false);
		} else {
			button_Home.setEnabled(true);
		} 

		List<ClassCalendarDay> month = cal.GetAllDaysOfMonth(activeMonth, activeYear);
		int index = 1;
		String idStringDay;
		int idTag;
		TextView tvDay = null;
		for (ClassCalendarDay monthDay : month) {
			/* den Startindex für die Anzeige ermitteln und ggf. die
			 * ersten nicht benötigten Tage ausblenden */
			if (index == 1) {
				for (int i=0;i<7;i++) {
					try {
						idStringDay = "month_day_" + Integer.toString(index);
						idTag = R.id.class.getField(idStringDay).getInt(null);
						tvDay = (TextView)findViewById(idTag);
						tvDay.setVisibility(View.VISIBLE);
					} catch (Exception e) {
					}
					if (monthDay.getWochentag().equals(days[i])) {
						String dayValue = Integer.toString(Integer.parseInt(monthDay.getDatum_show().substring(0,2)));
						tvDay.setText(dayValue);
						setDayBackground(monthDay,tvDay);
						calDays[index-1] = monthDay;
						index++;
						break;
					} else {
						try {
							tvDay.setVisibility(View.INVISIBLE);
							index++;
						} catch (Exception e) {
						}
					}
				}
			} else {
				try {
					idStringDay = "month_day_" + Integer.toString(index);
					idTag = R.id.class.getField(idStringDay).getInt(null);
					tvDay = (TextView)findViewById(idTag);
					tvDay.setVisibility(View.VISIBLE);
					String dayValue = Integer.toString(Integer.parseInt(monthDay.getDatum_show().substring(0,2)));
					tvDay.setText(dayValue);
					setDayBackground(monthDay,tvDay);
					calDays[index-1] = monthDay;
					index++;
				} catch (Exception e) {
				}
			}
		}
		/* Letzte nicht benötigte Felder ausblenden */
		for (int i=index;i<38;i++) {
			try {
				idStringDay = "month_day_" + Integer.toString(i);
				idTag = R.id.class.getField(idStringDay).getInt(null);
				tvDay = (TextView)findViewById(idTag);
				tvDay.setVisibility(View.INVISIBLE);
			} catch (Exception e) {
			}
		}
	}

	/* Textfarbe und Hintergrundimage setzen
	 * die Zahl des aktuellen Tags wird rot dargestellt */
	private void setDayBackground(ClassCalendarDay day, TextView tvDay) {
		//Textfarbe
		if (day.getDatum_sort().equals(ClassCalendar.getToday().getDatum_sort())){
			tvDay.setTextColor(getResources().getColor(R.color.col_red));
		} else {
			tvDay.setTextColor(getResources().getColor(R.color.col_black));
		}
		String backID = "day_unmarked";
		if (day.getFerientag() == 1) {
			backID = "day_unmarked_holiday";
		}
		if (day.getWochentag().equals(getString(R.string.day_samstag)) || day.getWochentag().equals(getString(R.string.day_sonntag)) || day.getFeiertag() > 0) {
			backID = "day_unmarked_weekend";
		}
		//je nach activeView auf vorhandene Hausaufgaben/Notizen/Tests prüfen
		switch (ClassCalendar.getActiveView()) {
		case 1:
			//Auf Hausaufgaben prüfen (0 = egal ob erledigen oder abgeben
			if (hw.dayHasHomework(day.getId(),3)) {
				backID=backID.replace("unmarked", "marked");
			}
			break;
		case 2:
			//Auf Tests prüfen
			if (test.dayHasTests(day.getId())) {
				backID=backID.replace("unmarked", "marked");
			}
			break;
		case 3:
			//Auf Notizen prüfen
			if (note.dayHasNote(day.getId())) {
				backID=backID.replace("unmarked", "marked");
			}
			break;
		}
		try {
			int idBack = R.drawable.class.getField(backID).getInt(null);
			tvDay.setBackgroundResource(idBack);
		} catch (Exception e) {
		}
	}
	
	/* Den aktiven View festlegen und anzeigen */
	private void setActiveView(int actView) {
		/* zuerst alle Imgaes auf das nicht selektierte Image zurücksetzen */
		button_menu[0].setImageResource(R.drawable.menu_day_s);
		button_menu[1].setImageResource(R.drawable.menu_day_h);
		button_menu[2].setImageResource(R.drawable.menu_day_t);
		button_menu[3].setImageResource(R.drawable.menu_day_n);
		switch (actView) {
		case 0:
			button_menu[0].setImageResource(R.drawable.menu_day_s_selected);
			break;
		case 1:
			button_menu[1].setImageResource(R.drawable.menu_day_h_selected);
			break;
		case 2:
			button_menu[2].setImageResource(R.drawable.menu_day_t_selected);
			break;
		case 3:
			button_menu[3].setImageResource(R.drawable.menu_day_n_selected);
			break;
		}
	}

	private void daySelectedDialog(ClassCalendarDay curDay) {
		final Dialog dialog = new Dialog(CalendarMonthActivity.this, R.style.dialogtheme);
		final ClassCalendarDay day = curDay;
		dialog.setContentView(R.layout.day_selected_dialog);
		dialog.setCancelable(true);
		Button dayShow=(Button)dialog.findViewById(R.id.button_show);
		Button dayAdd=(Button)dialog.findViewById(R.id.button_add);
		Button dayBack=(Button)dialog.findViewById(R.id.button_back);
		switch (ClassCalendar.getActiveView()) {
		case 1:
			//Hausaufgaben
			dialog.setTitle(this.getString(R.string.tv_hausaufgaben));
			if (hw.dayHasHomework(day.getId(), 0)) dayShow.setEnabled(true);
			break;
		case 2:
			//Tests
			dialog.setTitle(this.getString(R.string.tv_tests));
			if (test.dayHasTests(day.getId())) dayShow.setEnabled(true);
			if ((isSchoolday() == false) || (day.getFeiertag() > 0) || (day.getFerientag() > 0)) {
				dayAdd.setEnabled(false);
			}
			break;
		case 3:
			//Notizen
			dialog.setTitle(this.getString(R.string.tv_notizen));
			if (note.dayHasNote(day.getId())) dayShow.setEnabled(true); 
			break;
		}
		
		dayShow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ClassCalendar.getActiveView() == 1) {
					if (ClassCalendar.getActiveHomeworkView() == 0) {
						if (hw.dayHasHomework(day.getId(), 1) == false) {
							ClassCalendar.setActiveHomeworkView(1);
						}
					}
					if (ClassCalendar.getActiveHomeworkView() == 1) {
						if (hw.dayHasHomework(day.getId(), 2) == false) {
							ClassCalendar.setActiveHomeworkView(0);
						}
					}
				}
				ClassCalendar.setActiveDayID(day.getId());
				((TabHost)getParent().findViewById(android.R.id.tabhost)).setCurrentTab(0);
				dialog.cancel();
			}
		});

		dayAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		 		Intent i;
				switch (ClassCalendar.getActiveView()) {
				case 1:
					//Hausaufgaben
			 		i = new Intent(CalendarMonthActivity.this, HomeworkAddActivity.class);
			 		i.putExtra("index", day.getId());
					startActivity(i);
					dialog.cancel();
					break;
				case 2:
					//Tests
			 		i = new Intent(CalendarMonthActivity.this, TestsAddActivity.class);
			 		i.putExtra("index", day.getId());
					startActivity(i);
					dialog.cancel();
					break;
				case 3:
					//Notizen
			 		i = new Intent(CalendarMonthActivity.this, NotesAddActivity.class);
			 		i.putExtra("index", day.getId());
					startActivity(i);
					dialog.cancel();
					break;
				}
			}
		});

		dayBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		dialog.show();
	}

	/* Prüft ob es ein schultag ist */
	private boolean isSchoolday() {
		
		if (isWeekend()) {
			if (cal_saturday == true) {
				if (day.getWochentag().equals(getString(R.string.day_samstag))) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	/* Prüft ob es ein Wochenende ist */
	private boolean isWeekend() {
		if (day.getWochentag().equals(getString(R.string.day_samstag)) || day.getWochentag().equals(getString(R.string.day_sonntag))) {
			return true;
		} else {
			return false;
		}
	}

	//Methode muss zum Erstellen des Menüs überschrieben werden
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {   
    	MenuInflater inflater = getMenuInflater();   
        inflater.inflate(R.menu.home_menu, menu);   
        return super.onCreateOptionsMenu(menu);   
    }   

    //Methode muss zum reagieren auf das Anklicken der Menü-Items überschrieben werden
    @Override  
    public boolean onOptionsItemSelected(MenuItem item) {
       	switch (item.getItemId()) {
        case R.id.menu_home_home:
			// Hauptmenü aufrufen
	 		Intent i = new Intent(CalendarMonthActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        	finish();
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
