package de.androloc.schoolplaner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class PlanListActivity extends Activity {

	private SharedPreferences calendar_Preferences;				//Kalender einstellungen
	private Boolean cal_saturday;								//Samstagsunterricht Ja/Nein 
	private String[] days;										//Tage für den Stundenplan
	private int curDay;											//Index des aktuellen Tages
	
	private TextView tvDay;										//Aktuller tag in der Titelleiste
	private ListView planListe;									//Listview mit den Stunden des Tages

	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	private PlanListAdapter listAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.plan_list_act);

	    String yearTitle = getTitle() + " " + ClassSettings.getActiveYearShow();
	    setTitle(yearTitle);

	    /*Kalender-Einstellingen initialisieren und Samstag-Unterricht prüfen
	      ------------------------------------------------------------------- */
	 	calendar_Preferences = PreferenceManager.getDefaultSharedPreferences(this);
	 	cal_saturday = calendar_Preferences.getBoolean("cal_saturday", false);
	    
	    /* Die gültigen Tage für den Stundenplan in das Array übergeben
	     * ------------------------------------------------------------ */
	 	Resources res = getResources();
	 	if (cal_saturday == true) {
		 	days= res.getStringArray(R.array.plan_days_s);
	 	} else {
		 	days = res.getStringArray(R.array.plan_days);
	 	}
	 	curDay = 0;	//aktueller Index
	 	
	 	/* Widget-Objekte 
	 	 * -------------- */
	 	tvDay = (TextView)findViewById(R.id.tv_plan_day);
	 	planListe = (ListView)findViewById(R.id.listViewPlan);
	 	planListe.setOnItemClickListener(listenerStunde);
	 	
	 	((ImageButton)findViewById(R.id.button_navi_next)).setOnClickListener(listenerNextDay);
	 	((ImageButton)findViewById(R.id.button_navi_previous)).setOnClickListener(listenerPreviousDay);
	 	
	 	/* Den ersten Tag anzeigen 
	 	 * ----------------------- */
	 	setCurrentDay();
	}

	/* Listener für Vor-Button 
	 * ------------------------*/
	OnClickListener listenerNextDay = new OnClickListener() {
		@Override
		public void onClick(View v) {
			curDay++;
			if (curDay == days.length) curDay = 0;
			setCurrentDay();
		}
	};
	/* Listener für Zurück-Button 
	 * --------------------------*/
	OnClickListener listenerPreviousDay = new OnClickListener() {
		@Override
		public void onClick(View v) {
			curDay--;
			if (curDay == -1) curDay = days.length - 1;
			setCurrentDay();
		}
	};
	
	/* Listener füe Click auf Listen-Item */
	OnItemClickListener listenerStunde = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
			//Activity zur Erfassung eines neuen Lehrers starten
			Intent i = new Intent(PlanListActivity.this, PlanEditActivity.class);
	 		i.putExtra("index", id);
			startActivity(i);
		}
	};

	/* Alle Daten des ausgewählten Tages anzeigen 
	 * ------------------------------------------*/ 
	private void setCurrentDay() {
		tvDay.setText(days[curDay]);
        db = new DB_DatabaseHelper();
        //SQL-Abfrage durchführen Cursor holen
        dbCursor = db.query(getString(R.string.table_stundenplan), getString(R.string.field_tag) + " = '" + days[curDay] + "'", getString(R.string.field_stunde)+ " ASC");
        startManagingCursor(dbCursor);
        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
        listAdapter = new PlanListAdapter(planListe.getContext(),dbCursor,true);
        planListe.setAdapter(listAdapter);
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
	 		Intent i = new Intent(PlanListActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        	finish();
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
