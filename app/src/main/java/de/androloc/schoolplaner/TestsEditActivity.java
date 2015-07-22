package de.androloc.schoolplaner;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TestsEditActivity extends Activity {

	private SharedPreferences calendar_Preferences;				//Kalender einstellungen
	private Boolean cal_saturday;								//Samstagsunterricht Ja/Nein 

	private Button buttonOK;
	private Button buttonBack;
	private ImageButton buttonSearchTestArt;
	private TextView tvFach;
	private EditText editArt;
	private EditText editBeschreibung;
	private EditText editNote;
	private EditText editDurchschnitt;
	private TextView tvTermin;
	private long testID, fachID;
	private ListView testartListe;
	private TestArtSearchAdapter testartListAdapter;
	private int idxTestArt;
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	private ClassCalendar cal;
	private ClassCalendarDay dayTermin;
	private ClassTests test;


	private ImageButton button_Next;
	private ImageButton button_Previous;
	private ImageButton button_Home;

	private RelativeLayout topicTermin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tests_edit);

	    String yearTitle = getTitle() + " " + ClassSettings.getActiveYearShow();
	    setTitle(yearTitle);
	
	    cal = new ClassCalendar(this);
	    
	    //Index des Datensatzes
	    //---------------------
	    testID = getIntent().getLongExtra("index", 0);
	    if (testID == 0) {
	    	finish();
	    }
	    test = new ClassTests(this);
	    if (test.findTestByID(testID) == false) finish();

	    
	    /*Kalender-Einstellingen initialisieren und Samstag-Unterricht prüfen
	      ------------------------------------------------------------------- */
	 	calendar_Preferences = PreferenceManager.getDefaultSharedPreferences(this);
	 	cal_saturday = calendar_Preferences.getBoolean("cal_saturday", false);
	 	
	 	/* Widget-Objekte */
	 	topicTermin = (RelativeLayout)findViewById(R.id.cal_testtermin_topic);
	 	tvFach = (TextView)findViewById(R.id.tv_fach);
	 	editArt = (EditText)findViewById(R.id.edit_test_art);
	    editBeschreibung = (EditText)findViewById(R.id.edit_test_beschreibung);
	    editNote = (EditText)findViewById(R.id.edit_test_note);
	    editDurchschnitt = (EditText)findViewById(R.id.edit_test_durchschnitt);
	    tvTermin = (TextView)findViewById(R.id.tv_test_testtag);

	    //On-Click für Übernehmen Button
        //------------------------------
        buttonOK = (Button)findViewById(R.id.button_test_update);
	    buttonOK.setOnClickListener(buttonOkListener);

	    //On-Click für Abbrechen Button
        //------------------------------
        ((Button)findViewById(R.id.button_test_back)).setOnClickListener(buttonCancelListener);

        //On-Click für Test-Art-Suche
        //---------------------------
	    buttonSearchTestArt = (ImageButton)findViewById(R.id.button_testart_search);
	    buttonSearchTestArt.setOnClickListener(testartSearchListener);

	    //Next, Previous und Home - Buttons
	    //---------------------------------
	    button_Next = (ImageButton)findViewById(R.id.button_navi_next);
	    button_Previous = (ImageButton)findViewById(R.id.button_navi_previous);
	    button_Home = (ImageButton)findViewById(R.id.button_navi_home);

	    //Listener für die navigations-Buttons
	    //------------------------------------
	    button_Next.setOnClickListener(buttonNextListener);
	    button_Previous.setOnClickListener(buttonPreviousListener);
	    button_Home.setOnClickListener(buttonHomeListener);

	    //Aktuelle werte übergeben und anzeigen
	    //-------------------------------------
	    dayTermin = cal.GetDayByID(test.getTermin());
	    editArt.setText(test.gettestArt());
	    editBeschreibung.setText(test.getBeschreibung());
	    editNote.setText(test.getNote());
	    editDurchschnitt.setText(test.getDurchschnitt());
	    fachID = test.getFach();
	    setFachCaption();
	    showTestDay();

	}

	//Heute
	OnClickListener buttonHomeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
		    dayTermin = ClassCalendar.getToday();
		    showTestDay();
		}
	};
	//Nächster Tag (Abgeben)
	OnClickListener buttonNextListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ClassCalendarDay lastDay = dayTermin;
			dayTermin = cal.GetNextDay(dayTermin.getDatum_sort());
		    if (dayTermin.getWochentag().equals(getString(R.string.day_samstag))) {
		    	if (cal_saturday == false) {
				    dayTermin = cal.GetNextDay(dayTermin.getDatum_sort());
		    	}
		    }
		    if (dayTermin.getWochentag().equals(getString(R.string.day_sonntag))) {
				dayTermin = cal.GetNextDay(dayTermin.getDatum_sort());
		    }
		    if (dayTermin == null) dayTermin = lastDay;
		    showTestDay();
		}
	};
	//Vorheriger Tag (Abgeben)
	OnClickListener buttonPreviousListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ClassCalendarDay lastDay = dayTermin;
		    dayTermin = cal.GetPreviousDay(dayTermin.getDatum_sort());
		    if (dayTermin.getWochentag().equals(getString(R.string.day_sonntag))) {
			    dayTermin = cal.GetPreviousDay(dayTermin.getDatum_sort());
		    	if (cal_saturday == false) {
				    dayTermin = cal.GetPreviousDay(dayTermin.getDatum_sort());
		    	}
		    }
		    if (dayTermin == null) dayTermin = lastDay;
		    showTestDay();
		}
	};

	/* Anzeigen der ausgewählten Tage für die Hausaufgaben */
	private void showTestDay() {
		
		tvTermin.setText(dayTermin.getWochentag()+ ", " + dayTermin.getDatum_show());
		setDayBackcolor(dayTermin, topicTermin);
		
		/*Navi-Buttons aktivieren/deaktivieren */
		//Previous
		if (dayTermin.getDatum_sort().equals(ClassCalendar.getFirstDay().getDatum_sort())) {
		    button_Previous.setEnabled(false);
		} else {
		    button_Previous.setEnabled(true);
		}
	
		//Next
		if (dayTermin.getDatum_sort().equals(ClassCalendar.getLastDay().getDatum_sort())) {
		    button_Next.setEnabled(false);
		} else {
		    button_Next.setEnabled(true);
		}
	
		//Home
		if (dayTermin.getDatum_sort().equals(ClassCalendar.getToday().getDatum_sort())) {
		    button_Home.setEnabled(false);
		} else {
		    button_Home.setEnabled(true);
		}
	}

	/* Hintergrundfarbe der tagesanzeige, ist bei allen Views einheitlich */
	private void setDayBackcolor(ClassCalendarDay day, RelativeLayout topic) {
		buttonOK.setVisibility(View.VISIBLE);
		topic.setBackgroundResource(R.color.col_day_default);
		if (day.getFeiertag() > 0) {
			buttonOK.setVisibility(View.GONE);
		}
		if (isWeekend(day) || (day.getFeiertag() > 0)) {
			topic.setBackgroundResource(R.color.col_day_weekend);
			return;
		}
		if (day.getFerientag() == 1) {
			topic.setBackgroundResource(R.color.col_day_holiday);
			buttonOK.setVisibility(View.GONE);
		}
	}
	
	//OnClick-Listener für Test-Art-Suche-Button
	//------------------------------------------
	OnClickListener testartSearchListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final Dialog dialog = new Dialog(TestsEditActivity.this, R.style.dialogtheme);
			dialog.setContentView(R.layout.testart_search_list);
			dialog.setCancelable(true);
			dialog.setTitle(R.string.cd_testart_search);
			testartListe = (ListView) dialog.findViewById(R.id.list_search_testart);
			buttonBack = (Button) dialog.findViewById(R.id.button_cancel_testart);
			db = new DB_DatabaseHelper();
	        //SQL-Abfrage durchführen Cursor holen
	        dbCursor = db.query(getString(R.string.table_testarten), null, null);
	        startManagingCursor(dbCursor);
	        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
	        testartListAdapter = new TestArtSearchAdapter(testartListe.getContext(),dbCursor,true);
	        testartListe.setAdapter(testartListAdapter);
	        /*Lehrer wurde aus der Liste ausgewählt*/
	        testartListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	@Override
		        public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
	        		dialog.cancel();
	        		idxTestArt = dbCursor.getColumnIndex(getString(R.string.field_testart));
	        		dbCursor.moveToPosition(pos);
	        		editArt.setText(dbCursor.getString(idxTestArt));
	        	}
			});
	        buttonBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});
	        /*Suchen-Dialog anzeigen*/
	        dialog.show();
		}
	};

	
	//OnClick-Listener für Übernehmen-Button
	//--------------------------------------
	OnClickListener buttonOkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//Neuen Hausaufgaben-Datensatz in Datenbank speichern
			//---------------------------------------------------
			ClassTests newTest = new ClassTests(TestsEditActivity.this);
			newTest.setFach(fachID);
			newTest.settestArt(editArt.getText().toString());
			newTest.setTermin(dayTermin.getId());
			newTest.setBeschreibung(editBeschreibung.getText().toString());
			newTest.setNote(editNote.getText().toString());
			newTest.setDurchschnitt(editDurchschnitt.getText().toString());
			newTest.updateTest(testID);
			finish();
		}
	};

	//OnClick-Listener für Abbrechen-Button
	//-------------------------------------
	OnClickListener buttonCancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Activity beenden
			finish();
		}
	};

	/* Prüft ob es ein Wochenende ist */
	private boolean isWeekend(ClassCalendarDay day) {
		if (day.getWochentag().equals(getString(R.string.day_samstag)) || day.getWochentag().equals(getString(R.string.day_sonntag))) {
			return true;
		} else {
			return false;
		}
	}

	/* den Text des Fachs auslesen und übergeben */
	private void setFachCaption() {
    	ClassFaecher fach = new ClassFaecher(this);
    	if (fach.findFachByID(fachID) == true) {
    		tvFach.setText(fach.getFach());
    		buttonOK.setEnabled(true);
    	} else {
    		tvFach.setText("");
    		buttonOK.setEnabled(false);
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
	 		Intent i = new Intent(TestsEditActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        	finish();
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
