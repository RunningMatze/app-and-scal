package de.androloc.schoolplaner;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.os.Bundle;

public class CalendarSettingsActivity extends PreferenceActivity {

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.pref_settings);
	    final ListPreference schuljahrListe = (ListPreference) getPreferenceManager().findPreference("cal_year");

	    //wurde bereits ein Schuljahr ausgewählt?
	    //---------------------------------------
	    try {
		    if (schuljahrListe.getValue().length() > 0){
				schuljahrListe.setSummary("Kalender für Schuljahr: " + schuljahrListe.getEntry());
		    } else {
				schuljahrListe.setSummary("Bitte wähle das gewünschte Schuljahr aus");
		    }
		} catch (Exception e) {
			schuljahrListe.setSummary("Bitte wähle das gewünschte Schuljahr aus");
		}

	    //Listener auf die Schuljahrliste
	    //-------------------------------
	    schuljahrListe.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				CharSequence value = (CharSequence) newValue;
				if (value.length() > 0) {
					String newYear = schuljahrListe.getEntries()[schuljahrListe.findIndexOfValue(newValue.toString())].toString();
					schuljahrListe.setSummary("Kalender für Schuljahr: " + newYear);
					// Hier den Code einfügen der bei OK ausgeführt werden soll
					ClassSettings settings = new ClassSettings(CalendarSettingsActivity.this);
					settings.Initialize(newValue.toString());
				} else {
					schuljahrListe.setSummary("Bitte wähle das gewünschte Schuljahr aus");
				}
				return true;
			}
		});
	}
}
