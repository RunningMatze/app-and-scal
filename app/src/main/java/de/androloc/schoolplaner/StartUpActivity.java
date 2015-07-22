
package de.androloc.schoolplaner;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class StartUpActivity extends Activity {

	SharedPreferences calendar_Preferences;		//Kalender einstellungen
	Boolean cal_created;						//Flag dass Kalender eingerichtet wurde 
	String cal_year;							//Aktuell eingestelltes Schuljahr 
	String cal_land;							//Länder-Kurzcode 
	Boolean cal_saturday;						//Samstagsunterricht Ja/Nein 
	private static final int DIALOG_NO_CAL = 1; //Kalender noch nicht eingerichtet
	
	private ClassSettings settings;				//Aktuelle Schuljahr und Datenbankeinstellungen
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    this.setTitle(getString(R.string.app_name));
	}
    @Override
    protected void onStart() {
    	super.onStart();
	    /*Kalender-Einstellingen initialisieren
	      ------------------------------------- */
	 	calendar_Preferences = PreferenceManager.getDefaultSharedPreferences(this);
	 	//Prüfen ob bereits der Kalender einfgestellt wurde
	 	//-------------------------------------------------
	 	cal_year = calendar_Preferences.getString("cal_year", "");
	 	cal_land = calendar_Preferences.getString("cal_land", "");
	 	cal_saturday = calendar_Preferences.getBoolean("cal_saturday",false);
	 	cal_created = calendar_Preferences.getBoolean("cal_created", false);
	 	
	 	/* Initialisierung der verfügbaren und eingestellten Schuljahre und des
	 	 * entsprechenden Datenbanknamens
	 	   --------------------------------------------------------------------*/
	 	settings = new ClassSettings(this);
	 	settings.Initialize(cal_year);	
	 	
	 	if (cal_created == false) {
			// Abfrage auf Initialisierung des Kalenders
	 		//==========================================
	 		showDialog(DIALOG_NO_CAL);
	 	} else {
			// Hauptmenü starten und diese Activity beenden
	 		//=============================================================================
	 		Intent i = new Intent(StartUpActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 		startActivity(i);
			finish();
			//=============================================================================
	 	}
    }
  //muss überschrieben werden wird beim erstellen einmalig aufgerufen
    @Override
    protected Dialog onCreateDialog(int id) {
    	
    	if (id == DIALOG_NO_CAL) {
    		//AlertDialog-Builder referenzieren
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		//AlertDialog-Builder konfigurieren
    		//Hier besser eine String-ID für den Titel verwenden, Hinweis kommt ja öfters vor
    		builder.setTitle("Kalender einrichten");         										
    		//Hier besser eine String-ID für die Nachricht verwenden.
    		builder.setMessage("Vor dem ersten Start muss der Kalender eingerichtet werden.\n" +
    				"Es werden einige Angaben zur korrekten Einrichtung des Kalenders benötigt.\n" +
    				"Kalender-Einrichtung jetzt durchführen?");		
    		//Dialog kann nicht mit der Back-Taste geschlossen werden.
    		builder.setCancelable(false);											
    		//OK/Ja Button
    		builder.setPositiveButton("ja", new DialogInterface.OnClickListener() { 
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				// Hier den Code einfügen der bei OK ausgeführt werden soll
    		 		Intent i = new Intent(StartUpActivity.this, CalendarPreferencesActivity.class);
    				startActivity(i);
    			}
    		});
    		//Abbrechen/nein Button
    		builder.setNegativeButton("nein", new DialogInterface.OnClickListener() {		
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				dialog.cancel();
    				finish();
    			}
    		});
    		//Dialog-Objekt zurückliefern
    		return builder.create();
    	}
    	return super.onCreateDialog(id);
    }
}
