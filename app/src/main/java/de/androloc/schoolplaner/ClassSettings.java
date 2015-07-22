package de.androloc.schoolplaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.res.AssetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class ClassSettings {

	private static Context scontext;
	private static AssetManager assManager;
	SharedPreferences calendar_Preferences;		//Kalender einstellungen

	//Das momentan eingestellte Schuljahr (z.B. "1213")
	private static String activeYear;
	//Das momentan eingestellte Schuljahr zum Anzeigen (z.B. "2012/2013")
	private static String activeYearShow;
	//Der Name der aktuellen Datenbank
	private static String databaseName;
	
	/* Getters und setters */
	public static String getActiveYear() {
		return activeYear;
	}
	public static String getActiveYearShow() {
		return activeYearShow;
	}
	public static String getDatabaseName() {
		return databaseName;
	}
	public static List<String> getAvailableYearsKey() {
		return availableYearsKey;
	}
	public static List<String> getAvailableYears() {
		return availableYears;
	}

	/* ================================================
	 * Alle implementierten Jahre, bei einem Update muss
	 * hier das neue Schuljahr hinzugefügt werden
	 ================================================== */
	private static final String implementedYearsKey[] = {"1213","1314","1415","1516"};
	private static final String implementedYears[] = {"2012/2013","2013/2014","2014/2015","2015/2016"};

	private static List<String> availableYearsKey;
	private static List<String> availableYears;

	/* Konstruktor */
	public ClassSettings(Context context) {
		super();
		scontext = context;
		availableYearsKey = new ArrayList<String>();
		availableYears = new ArrayList<String>();
	}

	/* Initialisierung der Settings */
	public void Initialize(String actYear) {
		/* 1. prüfen für welche implementierten Schuljahre auch
		 * eine SQL-Datei vorhanden ist, nur diese Jahre dürfen
		 * auch zur Auswahl freigegeben werden                
		 ======================================================  */
		assManager = scontext.getResources().getAssets();
		for (int i = 0; i < implementedYearsKey.length; i++) {
			String iniFile;
			iniFile = "specialdays" + implementedYearsKey[i] + ".cal";
			try {
				if (Arrays.asList(assManager.list("ini")).contains(iniFile)) {
					availableYearsKey.add(implementedYearsKey[i]);
					availableYears.add(implementedYears[i]);
				}
			} catch (Exception e) {
				//Datei existiert nicht
			}
		}
		/* 2. Falls kein aktuelles Jahr übergeben wurde, dann immer
		 * das zuletzt eingestellte aktuellste Jahr übernehmen
		 ======================================================  */
		if (actYear == null || actYear.equals("")) {
			actYear = availableYearsKey.get(availableYearsKey.size()-1);
			//Das aktuelle jahr in die Preferences zurückschreiben
	    	//Flag in den Preferences zurücksetzen
		 	calendar_Preferences = PreferenceManager.getDefaultSharedPreferences(scontext);
			SharedPreferences.Editor prefEdit = calendar_Preferences.edit();
	    	prefEdit.putString("cal_year", actYear);
	    	prefEdit.commit();
		}
		activeYear = actYear;
		activeYearShow = availableYears.get(availableYearsKey.indexOf(activeYear));
		/* 3. Den aktuellen Datenbanknamen initialisieren
		 ================================================  */
	    databaseName = "schoolplaner" + activeYear + ".db";
	}
}
