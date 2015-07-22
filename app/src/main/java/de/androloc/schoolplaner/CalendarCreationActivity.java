package de.androloc.schoolplaner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;

public class CalendarCreationActivity extends Activity {

    private List<String> listFeiertage;
    private List<String> listFerientage;
    private StructFeiertag feiertag;
    private StructFerientag ferientag;
	private DB_DatabaseHelper db;
	private ProgressBar calProgress;
    private CalendarIniReader calIniReader;
    private int progressStatus;
    
    //Handler des UI-Threads an den gepostet werden kann
    private Handler mHandler = new Handler();

    //private final static String TAG = CalenderCreationActivity.class.getSimpleName();
	SharedPreferences calendar_Preferences;		//Kalender einstellungen

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	    setContentView(R.layout.cal_create);
	 	calendar_Preferences = PreferenceManager.getDefaultSharedPreferences(this);

	 	//Fortschrittsanzeige
	    calProgress = (ProgressBar)findViewById(R.id.calCreateProgress);
	    progressStatus = 0;
	    
	    //Ländercode aus den Preferences
	    final String land = calendar_Preferences.getString("cal_land", "");

	    //Initialisierungsdatei mit dem aktuell eingestellten Schuljahr übergeben
	    String iniFile = "ini/specialdays" + ClassSettings.getActiveYear() + ".cal";
	    calIniReader = new CalendarIniReader(iniFile,land);
	    calIniReader.setContext(getApplicationContext());
	    if (calIniReader.ReadCalendarData() == false) {
	    	finish();
	    }
	    
	    //Hintergrundprozess für das Anlegen des Kalenders
	    //================================================
	    new Thread(new Runnable() {
			@Override
			public void run() {
			
		    	//Flag in den Preferences zurücksetzen
				SharedPreferences.Editor prefEdit = calendar_Preferences.edit();
		    	prefEdit.putBoolean("cal_created", false);
		    	prefEdit.commit();

		    	//Kalender einrichten
			    //===================
			    feiertag = new StructFeiertag();
			    ferientag = new StructFerientag();
			    StructDate datum = new StructDate();
			    Date dateStart;
			    Date dateEnde;
			    int anzahlTage = 0;
			    
			    //Erster und letzter Schultag
			    //---------------------------
			    String ersterTag = calIniReader.getErsterTag();
			    String letzterTag = calIniReader.getLetzterTag();
			    
			    //Listen holen
			    //------------
			    listFeiertage = calIniReader.getListFeiertage();
			    listFerientage = calIniReader.getListFerientage();
			    
			    //Datenbank Instanz
			    db = new DB_DatabaseHelper(getApplicationContext());
			    
			    //Alle Feiertage in die Datenbank einstellen
			    db.ClearTable(getString(R.string.table_feiertage));
			    for (String ftag: listFeiertage) {
			    	feiertag.SplitParts(ftag);
					Hashtable<String,String> fields = new Hashtable<String,String>();
					fields.put(getString(R.string.field_datum_sort), feiertag.getSortDate());
					fields.put(getString(R.string.field_datum_show), feiertag.getRealDate());
					fields.put(getString(R.string.field_bezeichnung), feiertag.getCaption());
					db.Add(getString(R.string.table_feiertage),fields);
			    }
			    
			    
			    //Kalender Instanz
			    Calendar cal = Calendar.getInstance();
			    
			    //Date's initialisieren und Anzahl der Tage ermitteln
			    ferientag.SplitParts(ersterTag);
			    datum.SplitDate(ferientag.getRealDate(), ".");
			    dateStart = new Date(datum.getJahr(),datum.getMonat(),datum.tag);
			    ferientag.SplitParts(letzterTag);
			    datum.SplitDate(ferientag.getRealDate(), ".");
			    dateEnde = new Date(datum.getJahr(),datum.getMonat(),datum.tag);
			    long difference = dateEnde.getTime() - dateStart.getTime();
			    anzahlTage = (int)(difference / (1000 * 60 * 60 * 24)) + 1;
			    
			    //Alle Tage in die Datenbank speichern
			    db.ClearTable(getString(R.string.table_kalender));
			    SimpleDateFormat realFormat = new SimpleDateFormat("dd.MM.yyyy");
		    	SimpleDateFormat sortFormat = new SimpleDateFormat("yyyy-MM-dd");
			    String realDate;
			    String sortDate;
				calProgress.setMax(anzahlTage);
			    cal.set(dateStart.getYear()+1900,dateStart.getMonth(),dateStart.getDate());
		    	for (int i=1;i<=anzahlTage;i++) {
			    	//Status des Fortschrittsbalkens
		    		progressStatus = i;
		    		//Datum formatieren und in Datenbank speichern
		    		realDate = realFormat.format(dateStart);
		    		sortDate = sortFormat.format(dateStart);
					Hashtable<String,String> fields = new Hashtable<String,String>();
					fields.put(getString(R.string.field_datum_sort), sortDate);
					fields.put(getString(R.string.field_datum_show), realDate);
					switch (cal.get(Calendar.DAY_OF_WEEK)) {
					case Calendar.MONDAY:
							fields.put(getString(R.string.field_wochentag), getString(R.string.day_montag));
						break;
					case Calendar.TUESDAY:
						fields.put(getString(R.string.field_wochentag), getString(R.string.day_dienstag));
					break;
					case Calendar.WEDNESDAY:
						fields.put(getString(R.string.field_wochentag), getString(R.string.day_mittwoch));
					break;
					case Calendar.THURSDAY:
						fields.put(getString(R.string.field_wochentag), getString(R.string.day_donnerstag));
					break;
					case Calendar.FRIDAY:
						fields.put(getString(R.string.field_wochentag), getString(R.string.day_freitag));
					break;
					case Calendar.SATURDAY:
						fields.put(getString(R.string.field_wochentag), getString(R.string.day_samstag));
					break;
					case Calendar.SUNDAY:
						fields.put(getString(R.string.field_wochentag), getString(R.string.day_sonntag));
					break;
					} 
					//Wochentag
					fields.put(getString(R.string.field_kw), Integer.toString(cal.get(Calendar.WEEK_OF_YEAR)));
					//Feiertag (_id aus der Datenbank, 0 = kein Feiertag)
					fields.put(getString(R.string.field_feiertag), Long.toString(db.getFeiertagID(sortDate)));
					//Ferientag ja/nein (1=ja)
					if (isFerientag(sortDate)) {
						fields.put(getString(R.string.field_ferientag),"1");
					} else {
						fields.put(getString(R.string.field_ferientag),"0");
					}
					db.Add(getString(R.string.table_kalender),fields);
		    		//Datum auf nächsteg Tag umstellen
					cal.add(Calendar.DATE, 1);
		    		dateStart = cal.getTime();
		    		//Fortschrittsanzeige aktualisieren
		    		mHandler.post(new Runnable() {
						@Override
						public void run() {
				    		calProgress.setProgress(progressStatus);
						}
					});
		    	}
		    	
		    	//=============================================================
		    	//Kalender wurde erstellt
		    	//Flag setzen und Activity schließen
		    	prefEdit.putBoolean("cal_created", true);
		    	prefEdit.commit();
		    	setResult(RESULT_OK);
		    	finish();
		    	//=============================================================
			}
		}).start();
	}

	//Abfrage ob ein Datum auf einen Ferientag fällt
	//----------------------------------------------
	private boolean isFerientag(String sortDate) {
		
	    ferientag = new StructFerientag();
		for (String ftag: listFerientage) {
	    	ferientag.SplitParts(ftag);
	    	if (ferientag.getSortDate().equals(sortDate)) {
	    		return true;
	    	}
	    }
		return false;
	}
}
