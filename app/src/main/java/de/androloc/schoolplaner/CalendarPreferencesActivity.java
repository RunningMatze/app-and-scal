package de.androloc.schoolplaner;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.ListPreference;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.Bundle;
import android.content.Intent;

public class CalendarPreferencesActivity extends PreferenceActivity {

    static final int CREATE_REQUEST = 0;
    boolean year_ok = false;
    boolean land_ok = false;

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.pref_calender);
	    setContentView(R.layout.pref_button);

	    final Button createButton = (Button) findViewById(R.id.button_createDB);
	    final ListPreference laenderListe = (ListPreference) getPreferenceManager().findPreference("cal_land");
	    final ListPreference schuljahrListe = (ListPreference) getPreferenceManager().findPreference("cal_year");
	    
	    //wurde bereits ein Schuljahr ausgewählt?
	    //---------------------------------------
	    try {
		    if (schuljahrListe.getValue().length() > 0){
				year_ok = true;
				if (land_ok == true)
				{
					createButton.setEnabled(true);
				} else {
					createButton.setEnabled(false);
				}
				schuljahrListe.setSummary("Kalender für Schuljahr: " + schuljahrListe.getEntry());
		    } else {
				year_ok = false;
			    createButton.setEnabled(false);
				schuljahrListe.setSummary("Bitte wähle das gewünschte Schuljahr aus");
		    }
		} catch (Exception e) {
			createButton.setEnabled(false);
			schuljahrListe.setSummary("Bitte wähle das gewünschte Schuljahr aus");
		}

	    //wurde bereits ein Land ausgewählt?
	    //----------------------------------
	    try {
		    if (laenderListe.getValue().length() > 0){
		    	land_ok = true;
				if (year_ok == true)
				{
					createButton.setEnabled(true);
				} else {
					createButton.setEnabled(false);
				}
				laenderListe.setSummary("Deine Auswahl: " + laenderListe.getEntry());
		    } else {
				land_ok = false;
		    	createButton.setEnabled(false);
				laenderListe.setSummary("Bitte wähle Dein Bundesland (für die Ferien-Einstellung im Kalender)");
		    }
		} catch (Exception e) {
			createButton.setEnabled(false);
			laenderListe.setSummary("Bitte wähle Dein Bundesland (für die Ferien-Einstellung im Kalender)");
		}
	    

	    //Listener auf die Schuljahrliste
	    //-------------------------------
	    schuljahrListe.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				CharSequence value = (CharSequence) newValue;
				if (value.length() > 0) {
					year_ok = true;
					if (land_ok == true)
					{
						createButton.setEnabled(true);
					} else {
						createButton.setEnabled(false);
					}
					schuljahrListe.setSummary("Kalender für Schuljahr: " +schuljahrListe.getEntries()[schuljahrListe.findIndexOfValue(newValue.toString())]);
				} else {
					createButton.setEnabled(false);
					schuljahrListe.setSummary("Bitte wähle das gewünschte Schuljahr aus");
				}
				return true;
			}
		});

	    //Listener auf die Länderliste
	    //----------------------------
	    laenderListe.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				CharSequence value = (CharSequence) newValue;
				if (value.length() > 0) {
					land_ok = true;
					if (year_ok == true)
					{
						createButton.setEnabled(true);
					} else {
						createButton.setEnabled(false);
					}
					laenderListe.setSummary("Deine Auswahl: " +laenderListe.getEntries()[laenderListe.findIndexOfValue(newValue.toString())]);
				} else {
					createButton.setEnabled(false);
					laenderListe.setSummary("Bitte wähle Dein Bundesland (für die Ferien-Einstellung im Kalender)");
				}
				return true;
			}
		});

	    //Listener auf Button zur Datenbankerstellung
	    //-------------------------------------------
	    createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        //Datenbank initilisieren - dadurch wird sie automatisch erstellt
			    DB_DatabaseHelper database;
				database = new DB_DatabaseHelper(v.getContext());
				try {
					database.close();
				} catch (Exception e) {
				}
				// Hier den Code einfügen der bei OK ausgeführt werden soll
				ClassSettings settings = new ClassSettings(CalendarPreferencesActivity.this);
				settings.Initialize(schuljahrListe.getValue());
				Intent i = new Intent(CalendarPreferencesActivity.this, CalendarCreationActivity.class);
				startActivityForResult(i,CREATE_REQUEST);
			}
		});
	}

	//Kalendererstellung abgeschlossen
	//================================
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_REQUEST) {
            if (resultCode == RESULT_OK) {
                //kalender wurde erstellt, Activity schließen 
                //===========================================
            	finish();
            }
        }
	}
}
