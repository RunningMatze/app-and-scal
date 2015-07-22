package de.androloc.schoolplaner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class NotesEditActivity extends Activity {

	private Button buttonOK;
	private TextView tvDatum;
	private EditText editNotiz;
	private long noteID;
	private ClassCalendar cal;
	private ClassCalendarDay dayNotiz;
	private ClassNotes note;

	private ImageButton button_Next;
	private ImageButton button_Previous;
	private ImageButton button_Home;

	private RelativeLayout topicDatum;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.notes_add);

	    String yearTitle = getTitle() + " " + ClassSettings.getActiveYearShow();
	    setTitle(yearTitle);

	    cal = new ClassCalendar(this);

	    //Index des Datensatzes
	    //---------------------
	    noteID = getIntent().getLongExtra("index", 0);
	    if (noteID == 0) {
	    	finish();
	    }
	    note = new ClassNotes(this);
	    if (note.findNoteByID(noteID) == false) finish();

	 	/* Widget-Objekte */
	 	topicDatum = (RelativeLayout)findViewById(R.id.cal_notes_topic);
	    tvDatum = (TextView)findViewById(R.id.tv_notes_datum);
	    editNotiz = (EditText)findViewById(R.id.edit_notes);
	    editNotiz.addTextChangedListener(noteChangedListener);
	    
	    //On-Click für Übernehmen Button
        //------------------------------
        buttonOK = (Button)findViewById(R.id.button_notes_add_ok);
	    buttonOK.setOnClickListener(buttonOkListener);
	    buttonOK.setText(R.string.button_ok);
	    buttonOK.setEnabled(true);

	    // On-Click für Abbrechen Button
        //------------------------------
        ((Button)findViewById(R.id.button_notes_add_cancel)).setOnClickListener(buttonCancelListener);

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
	    dayNotiz = cal.GetDayByID(note.getDatum());
	    editNotiz.setText(note.getNotiz());
	    showNoteDay();
	}

	//Text-Changed-Listener auf Notiz
	TextWatcher noteChangedListener = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			String note = editNotiz.getText().toString().trim();
			if (note.length() > 0) {
				buttonOK.setEnabled(true);
			} else {
				buttonOK.setEnabled(false);
			}
		}
	};
	
	//Heute
	OnClickListener buttonHomeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
		    dayNotiz = ClassCalendar.getToday();
		    showNoteDay();
		}
	};
	//Nächster Tag (Abgeben)
	OnClickListener buttonNextListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dayNotiz = cal.GetNextDay(dayNotiz.getDatum_sort());
		    showNoteDay();
		}
	};
	//Vorheriger Tag (Abgeben)
	OnClickListener buttonPreviousListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
		    dayNotiz = cal.GetPreviousDay(dayNotiz.getDatum_sort());
		    showNoteDay();
		}
	};

	/* Anzeigen der ausgewählten Tage für die Hausaufgaben */
	private void showNoteDay() {
		
		tvDatum.setText(dayNotiz.getWochentag()+ ", " + dayNotiz.getDatum_show());
		setDayBackcolor(dayNotiz, topicDatum);
		
		/*Navi-Buttons aktivieren/deaktivieren */
		//Previous
		if (dayNotiz.getDatum_sort().equals(ClassCalendar.getFirstDay().getDatum_sort())) {
		    button_Previous.setEnabled(false);
		} else {
		    button_Previous.setEnabled(true);
		}
	
		//Next
		if (dayNotiz.getDatum_sort().equals(ClassCalendar.getLastDay().getDatum_sort())) {
		    button_Next.setEnabled(false);
		} else {
		    button_Next.setEnabled(true);
		}
	
		//Home
		if (dayNotiz.getDatum_sort().equals(ClassCalendar.getToday().getDatum_sort())) {
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


	//OnClick-Listener für Übernehmen-Button
	//--------------------------------------
	OnClickListener buttonOkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//Neuen Hausaufgaben-Datensatz in Datenbank speichern
			//---------------------------------------------------
			ClassNotes newNote = new ClassNotes(NotesEditActivity.this);
			newNote.setDatum(dayNotiz.getId());
			newNote.setNotiz(editNotiz.getText().toString());
			newNote.updateNote(noteID);
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
	 		Intent i = new Intent(NotesEditActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        	finish();
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
