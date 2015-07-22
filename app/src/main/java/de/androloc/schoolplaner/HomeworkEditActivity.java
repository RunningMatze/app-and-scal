package de.androloc.schoolplaner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeworkEditActivity extends Activity {

	private SharedPreferences calendar_Preferences;				//Kalender einstellungen
	private Boolean cal_saturday;								//Samstagsunterricht Ja/Nein 

	private Button buttonOK;
	private TextView tvFach;
	private EditText editAufgaben;
	private TextView tvErledigen;
	private TextView tvAbgeben;
	private long homeworkID, fachID;
	private ClassCalendar cal;
	private ClassCalendarDay dayErledigen, dayAbgeben;
	private ClassHomework homework;
	
	private ImageButton button_Next_1;
	private ImageButton button_Previous_1;
	private ImageButton button_Home_1;
	private ImageButton button_Next_2;
	private ImageButton button_Previous_2;
	private ImageButton button_Home_2;

	private RelativeLayout topicErl;
	private RelativeLayout topicAbgabe;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.homework_edit);

	    String yearTitle = getTitle() + " " + ClassSettings.getActiveYearShow();
	    setTitle(yearTitle);

	    cal = new ClassCalendar(this);
	    
	    //Index des Datensatzes
	    //---------------------
	    homeworkID = getIntent().getLongExtra("index", 0);
	    if (homeworkID == 0) {
	    	finish();
	    }
	    homework = new ClassHomework(this);
	    if (homework.findHomeworkByID(homeworkID) == false) finish();
	    
	    /*Kalender-Einstellingen initialisieren und Samstag-Unterricht prüfen
	      ------------------------------------------------------------------- */
	 	calendar_Preferences = PreferenceManager.getDefaultSharedPreferences(this);
	 	cal_saturday = calendar_Preferences.getBoolean("cal_saturday", false);

	 	/* Widget-Objekte */
	 	topicErl = (RelativeLayout)findViewById(R.id.cal_hw_erl_topic);
	 	topicAbgabe = (RelativeLayout)findViewById(R.id.cal_hw_abgabe_topic);
	 	tvFach = (TextView)findViewById(R.id.tv_fach);
	    editAufgaben = (EditText)findViewById(R.id.edit_homework_aufgaben);
	    tvErledigen = (TextView)findViewById(R.id.tv_homework_erltag);
	    tvAbgeben = (TextView)findViewById(R.id.tv_homework_abgabetag);
	    
	    //On-Click für Übernehmen Button
        //------------------------------
        buttonOK = (Button)findViewById(R.id.button_ok);
	    buttonOK.setOnClickListener(buttonOkListener);

        //On-Click für Abbrechen Button
        //------------------------------
        ((Button)findViewById(R.id.button_homework_back)).setOnClickListener(buttonCancelListener);

	    //Next, Previous und Home - Buttons
	    //---------------------------------
	    button_Next_1 = (ImageButton)findViewById(R.id.button_navi_next_1);
	    button_Previous_1 = (ImageButton)findViewById(R.id.button_navi_previous_1);
	    button_Home_1 = (ImageButton)findViewById(R.id.button_navi_home_1);
	    button_Next_2 = (ImageButton)findViewById(R.id.button_navi_next_2);
	    button_Previous_2 = (ImageButton)findViewById(R.id.button_navi_previous_2);
	    button_Home_2 = (ImageButton)findViewById(R.id.button_navi_home_2);

	    //Listener für die navigations-Buttons
	    //------------------------------------
	    button_Next_1.setOnClickListener(buttonNextListener1);
	    button_Previous_1.setOnClickListener(buttonPreviousListener1);
	    button_Home_1.setOnClickListener(buttonHomeListener1);
	    button_Next_2.setOnClickListener(buttonNextListener2);
	    button_Previous_2.setOnClickListener(buttonPreviousListener2);
	    button_Home_2.setOnClickListener(buttonHomeListener2);

	    //Aktuelle werte übergeben und anzeigen
	    //-------------------------------------
	    dayErledigen = cal.GetDayByID(homework.getErledigenAm());
	    dayAbgeben = cal.GetDayByID(homework.getAbgabeAm());
	    editAufgaben.setText(homework.getAufgaben());
	    fachID = homework.getFach();
	    setFachCaption();
	    showHomeworkDays();
	}

	/* Navigations-Buttons-Listener 
	 * ---------------------------- */
	//Heute (Erledigen)
	OnClickListener buttonHomeListener1 = new OnClickListener() {
		@Override
		public void onClick(View v) {
		    dayErledigen = ClassCalendar.getToday();
		    dayAbgeben = cal.GetNextDay(dayErledigen.getDatum_sort());
		    if (dayAbgeben.getWochentag().equals(getString(R.string.day_samstag))) {
		    	if (cal_saturday == false) {
				    dayAbgeben = cal.GetNextDay(dayAbgeben.getDatum_sort());
		    	}
		    }
		    if (dayAbgeben.getWochentag().equals(getString(R.string.day_sonntag))) {
				dayAbgeben = cal.GetNextDay(dayAbgeben.getDatum_sort());
		    }
		    if (dayAbgeben == null) dayAbgeben = dayErledigen;
		    showHomeworkDays();
		}
	};
	//Nächster Tag (Erledigen)
	OnClickListener buttonNextListener1 = new OnClickListener() {
		@Override
		public void onClick(View v) {
		    dayErledigen = cal.GetNextDay(dayErledigen.getDatum_sort());
		    if (dayAbgeben.getId() <= dayErledigen.getId()) {
			    dayAbgeben = cal.GetNextDay(dayErledigen.getDatum_sort());
			    if (dayAbgeben.getWochentag().equals(getString(R.string.day_samstag))) {
			    	if (cal_saturday == false) {
					    dayAbgeben = cal.GetNextDay(dayAbgeben.getDatum_sort());
			    	}
			    }
			    if (dayAbgeben.getWochentag().equals(getString(R.string.day_sonntag))) {
					dayAbgeben = cal.GetNextDay(dayAbgeben.getDatum_sort());
			    }
			    if (dayAbgeben == null) dayAbgeben = dayErledigen;
		    }
		    showHomeworkDays();
		}
	};
	//Vorheriger Tag (Erledigen)
	OnClickListener buttonPreviousListener1 = new OnClickListener() {
		@Override
		public void onClick(View v) {
		    dayErledigen = cal.GetPreviousDay(dayErledigen.getDatum_sort());
		    showHomeworkDays();
		}
	};
	//Heute (Abgeben)
	OnClickListener buttonHomeListener2 = new OnClickListener() {
		@Override
		public void onClick(View v) {
		    dayErledigen = ClassCalendar.getToday();
		    dayAbgeben = dayErledigen;
		    showHomeworkDays();
		}
	};
	//Nächster Tag (Abgeben)
	OnClickListener buttonNextListener2 = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ClassCalendarDay lastDay = dayAbgeben;
			dayAbgeben = cal.GetNextDay(dayAbgeben.getDatum_sort());
		    if (dayAbgeben.getWochentag().equals(getString(R.string.day_samstag))) {
		    	if (cal_saturday == false) {
				    dayAbgeben = cal.GetNextDay(dayAbgeben.getDatum_sort());
		    	}
		    }
		    if (dayAbgeben.getWochentag().equals(getString(R.string.day_sonntag))) {
				dayAbgeben = cal.GetNextDay(dayAbgeben.getDatum_sort());
		    }
		    if (dayAbgeben == null) dayAbgeben = lastDay;
		    showHomeworkDays();
		}
	};
	//Vorheriger Tag (Abgeben)
	OnClickListener buttonPreviousListener2 = new OnClickListener() {
		@Override
		public void onClick(View v) {
		    dayAbgeben = cal.GetPreviousDay(dayAbgeben.getDatum_sort());
		    if (dayErledigen.getId() > dayAbgeben.getId()) {
		    	dayErledigen = dayAbgeben;
		    }
		    showHomeworkDays();
		}
	};

	/* Anzeigen der ausgewählten Tage für die Hausaufgaben */
	private void showHomeworkDays() {
		
		tvErledigen.setText(dayErledigen.getWochentag()+ ", " + dayErledigen.getDatum_show());
		tvAbgeben.setText(dayAbgeben.getWochentag() + ", " + dayAbgeben.getDatum_show());
		setDayBackcolor(dayErledigen, topicErl);
		setDayBackcolor(dayAbgeben, topicAbgabe);
		
		/*Navi-Buttons aktivieren/deaktivieren */
		//Previous
		if (dayErledigen.getDatum_sort().equals(ClassCalendar.getFirstDay().getDatum_sort())) {
		    button_Previous_1.setEnabled(false);
		} else {
		    button_Previous_1.setEnabled(true);
		}
		if (dayAbgeben.getDatum_sort().equals(ClassCalendar.getFirstDay().getDatum_sort())) {
		    button_Previous_2.setEnabled(false);
		} else {
		    button_Previous_2.setEnabled(true);
		}
	
		//Next
		if (dayErledigen.getDatum_sort().equals(ClassCalendar.getLastDay().getDatum_sort())) {
		    button_Next_1.setEnabled(false);
		} else {
		    button_Next_1.setEnabled(true);
		}
		if (dayAbgeben.getDatum_sort().equals(ClassCalendar.getLastDay().getDatum_sort())) {
		    button_Next_2.setEnabled(false);
		} else {
		    button_Next_2.setEnabled(true);
		}

		//Home
		if (dayErledigen.getDatum_sort().equals(ClassCalendar.getToday().getDatum_sort())) {
		    button_Home_1.setEnabled(false);
		} else {
		    button_Home_1.setEnabled(true);
		}
		if (dayAbgeben.getDatum_sort().equals(ClassCalendar.getToday().getDatum_sort())) {
		    button_Home_2.setEnabled(false);
		} else {
		    button_Home_2.setEnabled(true);
		}
	}
	
	/* Hintergrundfarbe der tagesanzeige, ist bei allen Views einheitlich */
	private void setDayBackcolor(ClassCalendarDay day, RelativeLayout topic) {
		topic.setBackgroundResource(R.color.col_day_default);
		if (isWeekend(day) || (day.getFeiertag() > 0)) {
			topic.setBackgroundResource(R.color.col_day_weekend);
			return;
		}
		if (day.getFerientag() == 1) {
			topic.setBackgroundResource(R.color.col_day_holiday);
		}
	}

	//OnClick-Listener für Übernehmen-Button
	//--------------------------------------
	OnClickListener buttonOkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//Neuen Hausaufgaben-Datensatz in Datenbank speichern
			//---------------------------------------------------
			homework.setErledigenAm(dayErledigen.getId());
			homework.setAbgabeAm(dayAbgeben.getId());
			homework.setAufgaben(editAufgaben.getText().toString());
			homework.updateHomework(homeworkID);
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
	 		Intent i = new Intent(HomeworkEditActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        	finish();
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}