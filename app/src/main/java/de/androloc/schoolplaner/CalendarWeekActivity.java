package de.androloc.schoolplaner;

import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class CalendarWeekActivity extends Activity {

	private SharedPreferences calendar_Preferences;				//Kalender einstellungen
	private Boolean cal_saturday;								//Samstagsunterricht Ja/Nein 

	ClassCalendar cal;											//Kalender-Objekt
	ClassCalendarDay day;										//Tag-Objekt
	ClassCalendarDay firstDayOfWeek;
	ClassCalendarDay lastDayOfWeek;
	int activeWeek;
	int thisWeek;
	ImageButton button_Next;
	ImageButton button_Previous;
	ImageButton button_Home;
	ImageView button_menu[] = new ImageView[4];
	ImageView button_menu_erl;
	ImageView button_menu_ab;
	TextView tv_week;
	TextView tv_week_from;
	TextView tv_week_to;
	ViewFlipper flipWeekViews;
	boolean moSpecialDay = false;
	boolean diSpecialDay = false;
	boolean miSpecialDay = false;
	boolean doSpecialDay = false;
	boolean frSpecialDay = false;
	boolean saSpecialDay = false;
	
	/* Objekte für Datenbank und Tabellen */
	DB_DatabaseHelper db;
	private Cursor dbCursor;

	/* Hausaufgaben-Adapter für Montag bis Sonntag */
	ListView dayHomeworkListe; 
	DayHomeworkListAdapter dayHomeworkAdapter;

	/* Tests-Adapter für Montag bis Sonntag */
	ListView dayTestsListe; 
	DayTestListAdapter dayTestsAdapter;

	/* Notizen-Adapter für Montag bis Sonntag */
	ListView dayNotesListe; 
	DayNotesListAdapter dayNotesAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar_week);

	    /*Kalender-Einstellingen initialisieren und Samstag-Unterricht prüfen
	      ------------------------------------------------------------------- */
	 	calendar_Preferences = PreferenceManager.getDefaultSharedPreferences(this);
	 	cal_saturday = calendar_Preferences.getBoolean("cal_saturday", false);

	 	/* Falls kein Samstagunterricht, dann in der Stundenplantabelle
	 	 * die Samstags-Spalte ausblenden
	 	 --------------------------------------------------------------*/
	 	if (!cal_saturday) {
	 		hideSaturdayColumn();
	 	}
	 	
	 	/* Benütigte Objekte für die Kalenderdarstellung und
	     * die Kalendernavigation
	     --------------------------------------------------- */
	    cal = new ClassCalendar(this);
	
	    /* Topic-Anzeige, Tag und datum */
		tv_week = (TextView)findViewById(R.id.tv_cal_week_week); 
	    tv_week_from = (TextView)findViewById(R.id.tv_cal_week_from); 
	    tv_week_to = (TextView)findViewById(R.id.tv_cal_week_to); 

	    /* Listen für die Tagesanzeige der Hausaufgaben */
	    dayHomeworkListe = (ListView)findViewById(R.id.list_hw_week);

	    /* Listen für die Tagesanzeige der Notizen */
	    dayNotesListe = (ListView)findViewById(R.id.list_notes_week);

	    /* Listen für die Tagesanzeige der Tests */
	    dayTestsListe = (ListView)findViewById(R.id.list_tests_week);

	    /* Next, Previous und Home - Buttons */
	    button_Next = (ImageButton)findViewById(R.id.button_navi_next);
	    button_Previous = (ImageButton)findViewById(R.id.button_navi_previous);
	    button_Home = (ImageButton)findViewById(R.id.button_navi_home);
	    
	    /*Mini-Menü-Buttons zum Umschalten der verschiedenen Views*/
	    button_menu[0] = (ImageView)findViewById(R.id.menu_week_s);
	    button_menu[1] = (ImageView)findViewById(R.id.menu_week_h);
	    button_menu[2] = (ImageView)findViewById(R.id.menu_week_t);
	    button_menu[3] = (ImageView)findViewById(R.id.menu_week_n);
	    button_menu_erl = (ImageView)findViewById(R.id.menu_day_erl);
	    button_menu_ab = (ImageView)findViewById(R.id.menu_day_ab);
	    
	    /*View-Flipper für die verschiedenen Unter-views*/
	    flipWeekViews = (ViewFlipper)findViewById(R.id.viewFlipperWeek);

	    /* Aktuelle Woche ermitteln */
	    if (ClassCalendar.getActiveDayID() > 0) {
		    day = cal.GetDayByID(ClassCalendar.getActiveDayID());
	    } else {
		    day = ClassCalendar.getToday();
	    }
    	if (day == null) {
    		finish();
    	}
	    activeWeek = day.getKw();
    	thisWeek=ClassCalendar.getToday().getKw();
    	showWeekData();

    	/* Listener für die navigations-Buttons */
	    button_Next.setOnClickListener(buttonNextListener);
	    button_Previous.setOnClickListener(buttonPreviousListener);
	    button_Home.setOnClickListener(buttonHomeListener);

	    /* Listener für die Mini-Menü-Buttons */
	    button_menu[0].setOnClickListener(buttonPlanListener);
	    button_menu[1].setOnClickListener(buttonHausaufgabenListener);
	    button_menu[2].setOnClickListener(buttonTestsListener);
	    button_menu[3].setOnClickListener(buttonNotizenListener);
	    button_menu_erl.setOnClickListener(buttonErledigenListener);
	    button_menu_ab.setOnClickListener(buttonAbgebenListener);

	    /* Aktuellen View aktivieren */
	    setActiveView(ClassCalendar.getActiveView());

	}

	/* Falls Activity neu aufgerufen wird und ein bestimmter tag angezeigt werden soll */
	@Override
	protected void onResume() {
		try {
			showWeekData();
		    setActiveView(ClassCalendar.getActiveView());
			super.onResume();
		} catch (Exception e) {
		}
	};

	/* Navigations-Buttons-Listener 
	 * ---------------------------- */
	//Heute
	OnClickListener buttonHomeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			activeWeek = thisWeek;
			showWeekData();
		}
	};
	//Nächste Woche
	OnClickListener buttonNextListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			day = cal.GetNextDay(lastDayOfWeek.getDatum_sort());
		    activeWeek = day.getKw();
			showWeekData();
		}
	};
	//Vorherige Woche
	OnClickListener buttonPreviousListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			day = cal.GetPreviousDay(firstDayOfWeek.getDatum_sort());
		    activeWeek = day.getKw();
			showWeekData();
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
				showWeekData();
			}
		}
	};
	OnClickListener buttonHausaufgabenListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ClassCalendar.getActiveView() != 1) {
				ClassCalendar.setActiveView(1);
				setActiveView(ClassCalendar.getActiveView());
				showWeekData();
			}
		}
	};
	OnClickListener buttonTestsListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ClassCalendar.getActiveView() != 2) {
				ClassCalendar.setActiveView(2);
				setActiveView(ClassCalendar.getActiveView());
				showWeekData();
			}
		}
	};
	OnClickListener buttonNotizenListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ClassCalendar.getActiveView() != 3) {
				ClassCalendar.setActiveView(3);
				setActiveView(ClassCalendar.getActiveView());
				showWeekData();
			}
		}
	};
	OnClickListener buttonErledigenListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ClassCalendar.getActiveHomeworkView() != 0) {
				ClassCalendar.setActiveHomeworkView(0);
				setHomeworkView();
			}
		}
	};
	OnClickListener buttonAbgebenListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ClassCalendar.getActiveHomeworkView() != 1) {
				ClassCalendar.setActiveHomeworkView(1);
				setHomeworkView();
			}
		}
	};

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
			setHomeworkView();
			break;
		case 2:
			button_menu[2].setImageResource(R.drawable.menu_day_t_selected);
			refreshTestsAdapter();
			break;
		case 3:
			button_menu[3].setImageResource(R.drawable.menu_day_n_selected);
			refreshNotesAdapter();
			break;
		}
		/*zu View flippen*/ 
		flipWeekViews.setDisplayedChild(actView);
	}

	/* erledigen oder abgeben Button für Hausaufgaben aktivieren 
	 * und die entsprechende Liste anzeigen
	 * ---------------------------------------------------------*/
	private void setHomeworkView() {
		switch (ClassCalendar.getActiveHomeworkView()) {
		case 0:
			button_menu_erl.setImageResource(R.drawable.menu_day_erl_selected);
			button_menu_ab.setImageResource(R.drawable.menu_day_ab);
			break;
		case 1:
			button_menu_erl.setImageResource(R.drawable.menu_day_erl);
			button_menu_ab.setImageResource(R.drawable.menu_day_ab_selected);
			break;
		}
		refreshHomeworkAdapter();
	}

	/* Alle relevanten Daten der aktuellen Woche an die UI übergeben
	 * ------------------------------------------------------------- */
	private void showWeekData() {

		if (activeWeek == 0) {
			finish();
		}
		
		/* erster und letzter tag der Woche */
	    firstDayOfWeek = cal.GetFirstDayOfWeek(activeWeek);
	    lastDayOfWeek = cal.GetLastDayOfWeek(activeWeek);

	    /* Woche anzeigen */
		tv_week.setText(Integer.toString(activeWeek)+ ". " + getString(R.string.tv_woche));
	    /* Datum von-bis anzeigen anzeigen */
		tv_week_from.setText(getString(R.string.tv_vom) + " " + firstDayOfWeek.getDatum_show());
		tv_week_to.setText(getString(R.string.tv_bis)+ " " + lastDayOfWeek.getDatum_show());
		
		/* Navigations-Buttons deaktivieren/deaktivieren */
		if (cal.GetLastDayOfWeek(activeWeek).getDatum_sort().equals(ClassCalendar.getLastDay().getDatum_sort())) {
			button_Next.setEnabled(false);
		} else {
			button_Next.setEnabled(true);
		}
		if (cal.GetFirstDayOfWeek(activeWeek).getDatum_sort().equals(ClassCalendar.getFirstDay().getDatum_sort())) {
			button_Previous.setEnabled(false);
		} else {
			button_Previous.setEnabled(true);
		} 
		if (activeWeek == thisWeek) {
			button_Home.setEnabled(false);
		} else {
			button_Home.setEnabled(true);
		} 
		
		/* Adapter für den jeweiligen View aktualisieren */ 
		switch (ClassCalendar.getActiveView()) {
		case 0:
			refreshPlanTable();
			break;
		case 1:
			refreshHomeworkAdapter();
			break;
		case 2:
			refreshTestsAdapter();
			break;
		case 3:
			refreshNotesAdapter();
			break;
		} 
	}

	/* Stundenplan-Tabelle aktualisieren */
	private void refreshPlanTable() {
		List<ClassCalendarDay> week = cal.GetAllDaysOfWeek(activeWeek);
		int index = 0;
		moSpecialDay = false;
		diSpecialDay = false;
		miSpecialDay = false;
		doSpecialDay = false;
		frSpecialDay = false;
		saSpecialDay = false;
		TextView dayTextView = null;
		for (ClassCalendarDay weekDay : week ) {
			index++;
			switch (index) {
			case 1:
				dayTextView=(TextView) findViewById(R.id.header_1_value);
				((LinearLayout)findViewById(R.id.row_week_1_day_1)).setBackgroundResource(R.color.col_day_default);
				if (weekDay.getFerientag() == 1) {
					((LinearLayout)findViewById(R.id.row_week_1_day_1)).setBackgroundResource(R.color.col_day_holiday);
					moSpecialDay = true;
				}
				if (weekDay.getFeiertag() > 0) {
					((LinearLayout)findViewById(R.id.row_week_1_day_1)).setBackgroundResource(R.color.col_day_weekend);
					moSpecialDay = true;
				}
				break;
			case 2:
				dayTextView=(TextView) findViewById(R.id.header_2_value);
				((LinearLayout)findViewById(R.id.row_week_1_day_2)).setBackgroundResource(R.color.col_day_default);
				if (weekDay.getFerientag() == 1) {
					((LinearLayout)findViewById(R.id.row_week_1_day_2)).setBackgroundResource(R.color.col_day_holiday);
					diSpecialDay = true;
				}
				if (weekDay.getFeiertag() > 0) {
					((LinearLayout)findViewById(R.id.row_week_1_day_2)).setBackgroundResource(R.color.col_day_weekend);
					diSpecialDay = true;
				}
				break;
			case 3:
				dayTextView=(TextView) findViewById(R.id.header_3_value);
				((LinearLayout)findViewById(R.id.row_week_1_day_3)).setBackgroundResource(R.color.col_day_default);
				if (weekDay.getFerientag() == 1) {
					((LinearLayout)findViewById(R.id.row_week_1_day_3)).setBackgroundResource(R.color.col_day_holiday);
					miSpecialDay = true;
				}
				if (weekDay.getFeiertag() > 0) {
					((LinearLayout)findViewById(R.id.row_week_1_day_3)).setBackgroundResource(R.color.col_day_weekend);
					miSpecialDay = true;
				}
				break;
			case 4:
				dayTextView=(TextView) findViewById(R.id.header_4_value);
				((LinearLayout)findViewById(R.id.row_week_1_day_4)).setBackgroundResource(R.color.col_day_default);
				if (weekDay.getFerientag() == 1) {
					((LinearLayout)findViewById(R.id.row_week_1_day_4)).setBackgroundResource(R.color.col_day_holiday);
					doSpecialDay = true;
				}
				if (weekDay.getFeiertag() > 0) {
					((LinearLayout)findViewById(R.id.row_week_1_day_4)).setBackgroundResource(R.color.col_day_weekend);
					doSpecialDay = true;
				}
				break;
			case 5:
				dayTextView=(TextView) findViewById(R.id.header_5_value);
				((LinearLayout)findViewById(R.id.row_week_1_day_5)).setBackgroundResource(R.color.col_day_default);
				if (weekDay.getFerientag() == 1) {
					((LinearLayout)findViewById(R.id.row_week_1_day_5)).setBackgroundResource(R.color.col_day_holiday);
					frSpecialDay = true;
				}
				if (weekDay.getFeiertag() > 0) {
					((LinearLayout)findViewById(R.id.row_week_1_day_5)).setBackgroundResource(R.color.col_day_weekend);
					frSpecialDay = true;
				}
				break;
			case 6:
				dayTextView=(TextView) findViewById(R.id.header_6_value);
				((LinearLayout)findViewById(R.id.row_week_1_day_6)).setBackgroundResource(R.color.col_day_weekend);
				break;
			default:
				break;
			}
			if (dayTextView != null) {
				dayTextView.setText(weekDay.getDatum_show().substring(0,2));
				if (weekDay.getDatum_sort().equals(ClassCalendar.getToday().getDatum_sort())) {
					dayTextView.setTextColor(getResources().getColor(R.color.col_light_red));
					dayTextView.setTypeface(null, Typeface.BOLD);
				} else {
					dayTextView.setTextColor(getResources().getColor(R.color.col_yellow));
					dayTextView.setTypeface(null, Typeface.NORMAL);
				}
			}
		}
		
		ClassPlan tmpPlan = new ClassPlan(CalendarWeekActivity.this);
		ClassFaecher tmpFach = new ClassFaecher(CalendarWeekActivity.this);
		dbCursor = tmpPlan.getPlanCursor();
		if (dbCursor != null) {
			for (int i=0; i<dbCursor.getCount();i++) {
				String tag = dbCursor.getString(dbCursor.getColumnIndex(getString(R.string.field_tag)));
	            int stunde = dbCursor.getInt(dbCursor.getColumnIndex(getString(R.string.field_stunde)));
				String beginn = dbCursor.getString(dbCursor.getColumnIndex(getString(R.string.field_beginn)));
				String ende = dbCursor.getString(dbCursor.getColumnIndex(getString(R.string.field_ende)));
				long fachID = dbCursor.getLong(dbCursor.getColumnIndex(getString(R.string.field_fach)));
				if (tmpFach.findFachByID(fachID) == false) {
					tmpFach.setKurzcode(getString(R.string.tv_undefined));
				}
				String idString = "row_d";
				boolean specialDay = false;
				/*Montag*/
				if (tag.equals(getString(R.string.day_montag))) {
					idString += "1";
					specialDay = moSpecialDay;
				}
				/*Dienstag*/
				if (tag.equals(getString(R.string.day_dienstag))) {
					idString += "2";
					specialDay = diSpecialDay;
				}
				/*Mittwoch*/
				if (tag.equals(getString(R.string.day_mittwoch))) {
					idString += "3";
					specialDay = miSpecialDay;
				}
				/*Donnerstag*/
				if (tag.equals(getString(R.string.day_donnerstag))) {
					idString += "4";
					specialDay = doSpecialDay;
				}
				/*Freitag*/
				if (tag.equals(getString(R.string.day_freitag))) {
					idString += "5";
					specialDay = frSpecialDay;
				}
				/*Samstag*/
				if (tag.equals(getString(R.string.day_samstag))) {
					idString += "6";
					specialDay = saSpecialDay;
				}
				idString += "_s";
				idString += Integer.toString(stunde);
				String idStringFach = idString + "_fach";
				String idStringFrom = idString + "_from";
				String idStringTo = idString + "_to";
				boolean show = true;
				if (tag.equals(getString(R.string.day_samstag))) {
					if (cal_saturday == false) show = false;
				}
				if (show) {
					try {
						int idTag = R.id.class.getField(idStringFach).getInt(null);
						int idFrom = R.id.class.getField(idStringFrom).getInt(null);
						int idTo = R.id.class.getField(idStringTo).getInt(null);
						if ((fachID == 0) || specialDay) {
							((TextView)findViewById(idTag)).setTextColor(getResources().getColor(R.color.col_dark_blue));
							((TextView)findViewById(idTag)).setText(getString(R.string.tv_frei));
							((TextView)findViewById(idFrom)).setText("");
							((TextView)findViewById(idTo)).setText("");
						} else {
							((TextView)findViewById(idTag)).setTextColor(getResources().getColor(R.color.col_black));
							((TextView)findViewById(idTag)).setText(tmpFach.getKurzcode());
							((TextView)findViewById(idFrom)).setText(beginn);
							((TextView)findViewById(idTo)).setText(ende);
						}

					} catch (Exception e) {
					}
				}
				dbCursor.moveToNext();
			}
		} else {
		}
	}

	/* Samstags-Spalte im Stundenplan ausblenden */
	private void hideSaturdayColumn() {
		((LinearLayout)findViewById(R.id.row_week_1_day_6)).setVisibility(View.GONE);
	}

	/* Alle Adapter für die Tageslisten der Hausaufgaben-Wochenübersicht bereitstellen 
	 * -------------------------------------------------------------------------------*/
	private void refreshHomeworkAdapter() {
		/* ggf am Wochenede/Feiertag/Ferientag Layer ausblenden und Wochenend-Image-Layer einblenden
		 * -----------------------------------------------------------------------------------------*/
		db = new DB_DatabaseHelper();
        //SQL-Abfrage durchführen Cursor holen
		String hw_abfrage = getString(R.string.field_erledigen_am);
		if (ClassCalendar.getActiveHomeworkView() == 1) hw_abfrage = getString(R.string.field_abgabe_am); 
		dbCursor = db.query(getString(R.string.table_hausaufgaben), hw_abfrage + " >= " + firstDayOfWeek.getId() + " AND " + hw_abfrage + " <= " + lastDayOfWeek.getId(), hw_abfrage + " ASC");
        startManagingCursor(dbCursor);
        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
        dayHomeworkAdapter = new DayHomeworkListAdapter(dayHomeworkListe.getContext(),dbCursor,true);
        dayHomeworkAdapter.setShowDay(true);
        dayHomeworkListe.setAdapter(dayHomeworkAdapter);
	}

	/* Alle Adapter für die Tageslisten der Tests-Wochenübersicht bereitstellen 
	 * ------------------------------------------------------------------------*/
	private void refreshTestsAdapter() {
		db = new DB_DatabaseHelper();
        //SQL-Abfrage durchführen Cursor holen
		dbCursor = db.query(getString(R.string.table_tests),getString(R.string.field_kalendertag) + " >= " + firstDayOfWeek.getId() + " AND " + getString(R.string.field_kalendertag) + " <= " + lastDayOfWeek.getId(), getString(R.string.field_kalendertag) + " ASC");
        startManagingCursor(dbCursor);
        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
        dayTestsAdapter = new DayTestListAdapter(dayTestsListe.getContext(),dbCursor,true);
        dayTestsAdapter.setShowDay(true);
        dayTestsListe.setAdapter(dayTestsAdapter);
	}

	/* Alle Adapter für die Tageslisten der Notizen-Wochenübersicht bereitstellen 
	 * --------------------------------------------------------------------------*/
	private void refreshNotesAdapter() {
		db = new DB_DatabaseHelper();
        //SQL-Abfrage durchführen Cursor holen
		dbCursor = db.query(getString(R.string.table_notizen),getString(R.string.field_kalendertag) + " >= " + firstDayOfWeek.getId() + " AND " + getString(R.string.field_kalendertag) + " <= " + lastDayOfWeek.getId(), getString(R.string.field_kalendertag) + " ASC");
        startManagingCursor(dbCursor);
        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
        dayNotesAdapter = new DayNotesListAdapter(dayNotesListe.getContext(),dbCursor,true);
        dayNotesAdapter.setShowDay(true);
        dayNotesListe.setAdapter(dayNotesAdapter);
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
	 		Intent i = new Intent(CalendarWeekActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        	finish();
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
