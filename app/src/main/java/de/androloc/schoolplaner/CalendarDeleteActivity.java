package de.androloc.schoolplaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class CalendarDeleteActivity extends Activity {

	private final Context context = this;
	private DB_DatabaseHelper db;
	SharedPreferences calendar_Preferences;		//Kalender einstellungen
	ClassCalendar cal;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar_delete);

	    String yearTitle = getTitle() + " " + ClassSettings.getActiveYearShow();
	    setTitle(yearTitle);

	    calendar_Preferences = PreferenceManager.getDefaultSharedPreferences(this);

	 	((Button)findViewById(R.id.button_back)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	 	
	 	((Button)findViewById(R.id.button_delete_db)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
	        	//Dialog mit Abfrage zum Löschen der Datenbank
	        	//============================================
				//AlertDialog-Builder referenzieren
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				//AlertDialog-Builder konfigurieren
				builder.setTitle(R.string.button_delete_bd);         										
				builder.setMessage(R.string.msg_areyousure);         										
				builder.setCancelable(true);											
				//OK/Ja Button
				builder.setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() { 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Datenbank initilisieren
					    db = new DB_DatabaseHelper(getApplicationContext());
					    db.close();
					    db.DeleteDB();
				    	//Flag in den Preferences zurücksetzen
						SharedPreferences.Editor prefEdit = calendar_Preferences.edit();
				    	prefEdit.putBoolean("cal_created", false);
				    	prefEdit.commit();
				    	//Datums-Merker im Kalender-Objekt zurücksetzen
				    	ClassCalendar.reset();
				    	//StartUpActivity
				    	Intent i = new Intent(CalendarDeleteActivity.this,StartUpActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				    	startActivity(i);
						finish();
					}
				});
				//Abbrechen/nein Button
				builder.setNegativeButton(R.string.msg_no, new DialogInterface.OnClickListener() {		
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				builder.show();
			}
		});
	}
}
