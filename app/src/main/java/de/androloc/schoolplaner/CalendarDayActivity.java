package de.androloc.schoolplaner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DigitalClock;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class CalendarDayActivity extends Activity {

	private SharedPreferences calendar_Preferences;				//Kalender einstellungen
	private Boolean cal_saturday;								//Samstagsunterricht Ja/Nein 
	private Boolean cal_clock;								    //Uhrzeit einblenden 

	ClassCalendar cal;
	ClassCalendarDay day;
	ClassFeiertage feiertag;
	TextView tv_day_Day;
	TextView tv_day_Date;
	ImageButton button_Next;
	ImageButton button_Previous;
	ImageButton button_Home;
	ImageView button_menu[] = new ImageView[4];
	ImageView button_menu_erl;
	ImageView button_menu_ab;
	TextView tv_Feiertag;
	TextView tv_Free;
	TextView tv_Test_Free;
	ViewFlipper flipDayViews;
	DigitalClock clock;

	/* Layouts für verschiedene Ansichten */
	RelativeLayout dayTopic;
	LinearLayout layoutDayPlanDefault;
	LinearLayout layoutDayPlanWeekend;
	LinearLayout layoutDayTestDefault;
	LinearLayout layoutDayTestWeekend;
	
	/*Adapter für die unterschiedlichen Listen*/
	ListView dayPlanListe;
	DayPlanListAdapter dayPlanAdapter;
	ListView dayHomeworkListe;
	DayHomeworkListAdapter dayHomeworkAdapter;
	ListView dayTestListe;
	DayTestListAdapter dayTestAdapter;
	ListView dayNotesListe;
	DayNotesListAdapter dayNotesAdapter;

	/* Objekte für Datenbank und Tabellen */
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar_day);

	    /*Kalender-Einstellingen initialisieren und Samstag-Unterricht prüfen
	      ------------------------------------------------------------------- */
	 	calendar_Preferences = PreferenceManager.getDefaultSharedPreferences(this);
	 	cal_saturday = calendar_Preferences.getBoolean("cal_saturday", false);
	 	cal_clock = calendar_Preferences.getBoolean("cal_clock", true);

	 	/* Benütigte Objekte für die Kalenderdarstellung und
	     * die Kalendernavigation
	     --------------------------------------------------- */
	    cal = new ClassCalendar(this);

	    /* Layouts für die Ansichten */
		layoutDayPlanDefault = (LinearLayout)findViewById(R.id.frameDayPlanDefault);
		layoutDayPlanWeekend = (LinearLayout)findViewById(R.id.frameDayPlanFree);
		layoutDayTestDefault = (LinearLayout)findViewById(R.id.frameDayTestDefault);
		layoutDayTestWeekend = (LinearLayout)findViewById(R.id.frameDayTestFree);
		tv_Feiertag = (TextView)findViewById(R.id.tv_feiertag);
		tv_Free = (TextView)findViewById(R.id.tv_free);
		tv_Test_Free = (TextView)findViewById(R.id.tv_test_free);
	    
	    /* Topic-Anzeige, Tag und Datum, sowie ggf. Uhrzeit */
	    dayTopic = (RelativeLayout)findViewById(R.id.cal_day_topic);
		tv_day_Day = (TextView)findViewById(R.id.tv_cal_day_dayshort); 
	    tv_day_Date = (TextView)findViewById(R.id.tv_cal_day_date); 
	    clock = (DigitalClock)findViewById(R.id.digitalClock1);
	    /* ggf. Uhrzeit ausblenden */
	    if (cal_clock == false) {
	    	clock.setVisibility(View.INVISIBLE);
	    }

	    /* Next, Previous und Home - Buttons */
	    button_Next = (ImageButton)findViewById(R.id.button_navi_next);
	    button_Previous = (ImageButton)findViewById(R.id.button_navi_previous);
	    button_Home = (ImageButton)findViewById(R.id.button_navi_home);
	    
	    /*Mini-Menü-Buttons zum Umschalten der verschiedenen Views*/
	    button_menu[0] = (ImageView)findViewById(R.id.menu_day_s);
	    button_menu[1] = (ImageView)findViewById(R.id.menu_day_h);
	    button_menu[2] = (ImageView)findViewById(R.id.menu_day_t);
	    button_menu[3] = (ImageView)findViewById(R.id.menu_day_n);
	    button_menu_erl = (ImageView)findViewById(R.id.menu_day_erl);
	    button_menu_ab = (ImageView)findViewById(R.id.menu_day_ab);
	    
	    /*View-Flipper für die verschiedenen Unter-views*/
	    flipDayViews = (ViewFlipper)findViewById(R.id.viewFlipperDay);
	    
	    /* List-Views der einzelnen Activities */
	    dayPlanListe = (ListView)findViewById(R.id.listViewDayPlan);
	    dayHomeworkListe = (ListView)findViewById(R.id.listViewDayHomework);
	    dayTestListe = (ListView)findViewById(R.id.listViewDayTests);
	    dayNotesListe = (ListView)findViewById(R.id.listViewDayNotes);
	    
	    /* Aktuellen Tag einlesen und anzeigen */
	    day = ClassCalendar.getToday();
	    showDayData();
	    
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
			if (ClassCalendar.getActiveDayID() > 0) {
				day = cal.GetDayByID(ClassCalendar.getActiveDayID());
			    showDayData();
			    setActiveView(ClassCalendar.getActiveView());
			}
			super.onResume();
		} catch (Exception e) {
		}
	};
	
	/* Aktuellen Tag zwischenspeichern */
	@Override
	protected void onPause() {
		try {
			ClassCalendar.setActiveDayID(day.getId());
			super.onPause();
		} catch (Exception e) {
		}
	};

	/* Navigations-Buttons-Listener 
	 * ---------------------------- */
	//Heute
	OnClickListener buttonHomeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			day = ClassCalendar.getToday();
			showDayData();
		}
	};
	//Nächster Tag
	OnClickListener buttonNextListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			day = cal.GetNextDay(day.getDatum_sort());
			showDayData();
		}
	};
	//Vorheriger Tag
	OnClickListener buttonPreviousListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			day = cal.GetPreviousDay(day.getDatum_sort());
			showDayData();
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
			}
		}
	};
	OnClickListener buttonHausaufgabenListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ClassCalendar.getActiveView() != 1) {
				ClassCalendar.setActiveView(1);
				setActiveView(ClassCalendar.getActiveView());
			}
		}
	};
	OnClickListener buttonTestsListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ClassCalendar.getActiveView() != 2) {
				ClassCalendar.setActiveView(2);
				setActiveView(ClassCalendar.getActiveView());
			}
		}
	};
	OnClickListener buttonNotizenListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ClassCalendar.getActiveView() != 3) {
				ClassCalendar.setActiveView(3);
				setActiveView(ClassCalendar.getActiveView());
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

	/* Alle relevanten Daten des aktuellen Tages an die UI übergeben
	 * ------------------------------------------------------------- */
	private void showDayData() {

		if (day == null) {
			finish();
		}
		
		/* Tag-Kurz und Datum */
		tv_day_Day.setText(day.getWochentag().substring(0, 2));
		tv_day_Date.setText(day.getDatum_show());
		
		/* Navigations-Buttons deaktivieren/deaktivieren */
		if (day.getDatum_sort().equals(ClassCalendar.getLastDay().getDatum_sort())) {
			button_Next.setEnabled(false);
		} else {
			button_Next.setEnabled(true);
		}
		if (day.getDatum_sort().equals(ClassCalendar.getFirstDay().getDatum_sort())) {
			button_Previous.setEnabled(false);
		} else {
			button_Previous.setEnabled(true);
		}
		if (day.getDatum_sort().equals(ClassCalendar.getToday().getDatum_sort())) {
			button_Home.setEnabled(false);
		} else {
			button_Home.setEnabled(true);
		}
	
		/* Hintergrundfarbe der Tagesanzeige anpassen */
		setDayBackcolor();

		/* Adapter für den jeweiligen View aktualisieren */
		switch (ClassCalendar.getActiveView()) {
		case 0:
			refreshPlanAdapter();
			break;
		case 1:
			refreshHomeworkAdapter();
			break;
		case 2:
			refreshTestAdapter();
			break;
		case 3:
			refreshNotesAdapter();
			break;
		}

	}

	/* Hintergrundfarbe der tagesanzeige, ist bei allen Views einheitlich */
	private void setDayBackcolor() {
		dayTopic.setBackgroundResource(R.color.col_day_default);
		tv_Feiertag.setVisibility(View.GONE);
		if (isWeekend() || (day.getFeiertag() > 0)) {
			dayTopic.setBackgroundResource(R.color.col_day_weekend);
			if (day.getFeiertag() > 0) {
				feiertag = new ClassFeiertage(this);
				if (feiertag.findFeiertagByID(day.getFeiertag())) {
					tv_Feiertag.setText(feiertag.getBezeichnung());
					tv_Feiertag.setVisibility(View.VISIBLE);
				}
			}
			return;
		}
		if (day.getFerientag() == 1) {
			dayTopic.setBackgroundResource(R.color.col_day_holiday);
		}
	}

	/* Stundenplan-Adapter mit aktuellem Tag refreshen 
	 * -----------------------------------------------*/
	private void refreshPlanAdapter() {
        
		/* ggf am Wochenede/Feiertag/Ferientag Layer ausblenden und Wochenend-Image-Layer einblenden
		 * -----------------------------------------------------------------------------------------*/
		if (day.getFeiertag() > 0) {
			layoutDayPlanDefault.setVisibility(View.GONE);
			layoutDayPlanWeekend.setVisibility(View.VISIBLE);
			tv_Free.setText(R.string.tv_feiertag);
			return;
		}
		if (day.getFerientag() == 1) {
			layoutDayPlanDefault.setVisibility(View.GONE);
			layoutDayPlanWeekend.setVisibility(View.VISIBLE);
			tv_Free.setText(R.string.tv_ferien);
			return;
		}
		if (isSchoolday()) {
			layoutDayPlanWeekend.setVisibility(View.GONE);
			layoutDayPlanDefault.setVisibility(View.VISIBLE);
			db = new DB_DatabaseHelper();
	        //SQL-Abfrage durchführen Cursor holen
	        dbCursor = db.query(getString(R.string.table_stundenplan), getString(R.string.field_tag) + " = '" + day.getWochentag() + "'", getString(R.string.field_stunde)+ " ASC");
	        startManagingCursor(dbCursor);
	        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
	        dayPlanAdapter = new DayPlanListAdapter(dayPlanListe.getContext(),dbCursor,true);
	        dayPlanListe.setAdapter(dayPlanAdapter);
		} else {
			layoutDayPlanDefault.setVisibility(View.GONE);
			layoutDayPlanWeekend.setVisibility(View.VISIBLE);
			tv_Free.setText(R.string.tv_wochenende);
		}
	}
	
	/* Hausaufgaben-Adapter mit aktuellem Tag refreshen 
	 * -----------------------------------------------*/
	private void refreshHomeworkAdapter() {
        
		/* ggf am Wochenede/Feiertag/Ferientag Layer ausblenden und Wochenend-Image-Layer einblenden
		 * -----------------------------------------------------------------------------------------*/
		db = new DB_DatabaseHelper();
        //SQL-Abfrage durchführen Cursor holen
		String hw_abfrage = getString(R.string.field_erledigen_am);
		if (ClassCalendar.getActiveHomeworkView() == 1) hw_abfrage = getString(R.string.field_abgabe_am); 
		dbCursor = db.query(getString(R.string.table_hausaufgaben), hw_abfrage + " = " + day.getId(), getString(R.string.field_erledigt) + " ASC");
        startManagingCursor(dbCursor);
        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
        dayHomeworkAdapter = new DayHomeworkListAdapter(dayHomeworkListe.getContext(),dbCursor,true);
        dayHomeworkAdapter.setShowDay(false);
        dayHomeworkListe.setAdapter(dayHomeworkAdapter);
	}

	/* Notizen-Adapter mit aktuellem Tag refreshen 
	 * -------------------------------------------*/
	private void refreshNotesAdapter() {
		db = new DB_DatabaseHelper();
        //SQL-Abfrage durchführen Cursor holen
        dbCursor = db.query(getString(R.string.table_notizen), getString(R.string.field_kalendertag) + " = " + day.getId(), getString(R.string.field_id)+ " ASC");
        startManagingCursor(dbCursor);
        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
        dayNotesAdapter = new DayNotesListAdapter(dayNotesListe.getContext(),dbCursor,true);
        dayNotesAdapter.setShowDay(false);
        dayNotesListe.setAdapter(dayNotesAdapter);
	}

	/* Tests-Adapter mit aktuellem Tag refreshen 
	 * -----------------------------------------*/
	private void refreshTestAdapter() {
        
		/* ggf am Wochenede/Feiertag/Ferientag Layer ausblenden und Wochenend-Image-Layer einblenden
		 * -----------------------------------------------------------------------------------------*/
		if (day.getFeiertag() > 0) {
			layoutDayTestDefault.setVisibility(View.GONE);
			layoutDayTestWeekend.setVisibility(View.VISIBLE);
			tv_Test_Free.setText(R.string.tv_feiertag);
			return;
		}
		if (day.getFerientag() == 1) {
			layoutDayTestDefault.setVisibility(View.GONE);
			layoutDayTestWeekend.setVisibility(View.VISIBLE);
			tv_Test_Free.setText(R.string.tv_ferien);
			return;
		}
		if (isSchoolday()) {
			layoutDayTestWeekend.setVisibility(View.GONE);
			layoutDayTestDefault.setVisibility(View.VISIBLE);
			db = new DB_DatabaseHelper();
	        //SQL-Abfrage durchführen Cursor holen
	        dbCursor = db.query(getString(R.string.table_tests), getString(R.string.field_kalendertag) + " = " + day.getId(), getString(R.string.field_fach)+ " ASC");
	        startManagingCursor(dbCursor);
	        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
	        dayTestAdapter = new DayTestListAdapter(dayTestListe.getContext(),dbCursor,true);
	        dayTestListe.setAdapter(dayTestAdapter);
		} else {
			layoutDayTestDefault.setVisibility(View.GONE);
			layoutDayTestWeekend.setVisibility(View.VISIBLE);
			tv_Test_Free.setText(R.string.tv_wochenende);
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
			refreshPlanAdapter();
			break;
		case 1:
			button_menu[1].setImageResource(R.drawable.menu_day_h_selected);
			setHomeworkView();
			break;
		case 2:
			button_menu[2].setImageResource(R.drawable.menu_day_t_selected);
			refreshTestAdapter();
			break;
		case 3:
			button_menu[3].setImageResource(R.drawable.menu_day_n_selected);
			refreshNotesAdapter();
			break;
		}
		/*zu View flippen*/ 
		flipDayViews.setDisplayedChild(actView);
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
	 		Intent i = new Intent(CalendarDayActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 		startActivity(i);
        	finish();
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
