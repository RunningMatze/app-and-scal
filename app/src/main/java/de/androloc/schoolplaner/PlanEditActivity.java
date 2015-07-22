package de.androloc.schoolplaner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.TimePicker;

public class PlanEditActivity extends Activity {

	private ClassPlan hour;
	private TextView hourHeader;
	private EditText editFach;
	private ImageButton buttonSearchFach;
	private TimePicker timeBegin;
	private TimePicker timeEnd;
	private EditText editRaum;
	private EditText editBemerkung;
	private Button planOK;
	private Button planCancel;
	private Button buttonBack;
	private long index, fachID;
	private ListView faecherListe;
	private PlanFachSearchAdapter faecherListAdapter;
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.plan_edit);
	    
	    String yearTitle = getTitle() + " " + ClassSettings.getActiveYearShow();
	    setTitle(yearTitle);

	    /* Index des Datensates */
	    index =  getIntent().getLongExtra("index", 0);
	    if (index == 0) {
	    	finish();
	    }
	    
	    /* uhrzeitauswahl auf 24h-System umstellen */
	    timeBegin = (TimePicker)findViewById(R.id.time_hour_begin);
	    timeEnd = (TimePicker)findViewById(R.id.time_hour_end);
	    timeBegin.setIs24HourView(true);
	    timeEnd.setIs24HourView(true);
	
	    /* alle anderen Widgets */
	    hourHeader = (TextView)findViewById(R.id.tv_edit_plan_header);
	    editFach = (EditText)findViewById(R.id.edit_fach_bezeichnung);
	    buttonSearchFach = (ImageButton)findViewById(R.id.button_fach_search);
	    editRaum = (EditText)findViewById(R.id.edit_plan_raum);
	    editBemerkung = (EditText)findViewById(R.id.edit_plan_bemerkungen);
	    planOK = (Button)findViewById(R.id.button_plan_edit_ok);
	    planCancel = (Button)findViewById(R.id.button_plan_edit_cancel);
	    
	    
	    /*Datensatz auslesen und an die widgets übergeben */
	    hour = new ClassPlan(this);
	    hour.findHourByID(index);
	    
	    /* Headerzeile anzeigen */
	    String headerText = hour.getTag() + ", " + Integer.toString(hour.getStunde()) + ". " + getString(R.string.tv_schulstunde);
	    hourHeader.setText(headerText);
	    
	    /* Fach id = 0 bedeutet diese Unterrichsstunde ist frei
	     * ---------------------------------------------------- */
	    fachID = hour.getFach();
	    if (fachID > 0) {
	    	setFachCaption();
	    	timeBegin.setCurrentHour(hour.getBegin_hour());
	    	timeBegin.setCurrentMinute(hour.getBegin_minute());
	    	timeEnd.setCurrentHour(hour.getEnd_hour());
	    	timeEnd.setCurrentMinute(hour.getEnd_minute());
	    	editRaum.setText(hour.getRaum());
	    	editBemerkung.setText(hour.getBemerkung());
	    	/* Übernehmen-Button freigeben */
	    	planOK.setEnabled(true);
	    } else {
	    	/* Übernehmen-Button sperren */
	    	planOK.setEnabled(false);
	    	/* suchen ob es bereits einen tag gibt an dem eine Zeit für diese Stunde
	    	 * erfasst wurde, wenn ja dann diese Zeit als Voreinstellung übernehmen */
	    	if (hour.searchDefaultTime(hour.getStunde())) {
		    	timeBegin.setCurrentHour(hour.getBegin_hour());
		    	timeBegin.setCurrentMinute(hour.getBegin_minute());
		    	timeEnd.setCurrentHour(hour.getEnd_hour());
		    	timeEnd.setCurrentMinute(hour.getEnd_minute());
	    	}
	    }
	    
	    /* Listener für die Widgets 
	     * ========================*/
	    editFach.addTextChangedListener(fachChangedListener);
	    planOK.setOnClickListener(okListener);
	    planCancel.setOnClickListener(cancelListener);
	    buttonSearchFach.setOnClickListener(fachSearchListener);
	    
	}


	//OnClick-Listener für Fachsuche-Button
	//-------------------------------------
	OnClickListener fachSearchListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final Dialog dialog = new Dialog(PlanEditActivity.this, R.style.dialogtheme);
			dialog.setContentView(R.layout.faecher_search_list);
			dialog.setCancelable(true);
			dialog.setTitle(R.string.cd_faecher_search);
			faecherListe = (ListView) dialog.findViewById(R.id.list_search_fach);
			buttonBack = (Button) dialog.findViewById(R.id.button_cancel_fachsearch);
			db = new DB_DatabaseHelper();
	        //SQL-Abfrage durchführen Cursor holen
	        dbCursor = db.query(getString(R.string.table_faecher), null, getString(R.string.field_fach)+ " ASC");
	        startManagingCursor(dbCursor);
	        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
	        faecherListAdapter = new PlanFachSearchAdapter(faecherListe.getContext(),dbCursor,true);
	        faecherListe.setAdapter(faecherListAdapter);
	        /*Lehrer wurde aus der Liste ausgewählt*/
	        faecherListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	@Override
		        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
	        		dialog.cancel();
	        		fachID = id;
	        		setFachCaption();
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

	/* Listener für Textänderung beim Fach */
	TextWatcher fachChangedListener = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().trim().length() > 0) {
		    	planOK.setEnabled(true);
			} else {
		    	planOK.setEnabled(false);
			}
		}
	};
	
	/*Listener für Übernehmen-Button */
	OnClickListener okListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ClassPlan updPlan = new ClassPlan(PlanEditActivity.this);
			updPlan.setTag(hour.getTag());
			updPlan.setStunde(hour.getStunde());
			updPlan.setFach(fachID);
			updPlan.setRaum(editRaum.getText().toString());
			updPlan.setBemerkung(editBemerkung.getText().toString());
			/* Zeitangaben zusammensetzen */
			updPlan.setBegin_hour(timeBegin.getCurrentHour());
			updPlan.setBegin_minute(timeBegin.getCurrentMinute());
			updPlan.setEnd_hour(timeEnd.getCurrentHour());
			updPlan.setEnd_minute(timeEnd.getCurrentMinute());
			updPlan.joinTimes();
			updPlan.updatePlan(index);
			finish();
		}
	};
	
	OnClickListener cancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	/* den Text des Fachs auslesen und übergeben */
	private void setFachCaption() {
    	ClassFaecher fach = new ClassFaecher(this);
    	if (fach.findFachByID(fachID) == true) {
    		editFach.setText(fach.getFach());
    	} else {
    		editFach.setText("");
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
	 		Intent i = new Intent(PlanEditActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        	finish();
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
