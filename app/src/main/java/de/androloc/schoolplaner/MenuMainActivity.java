package de.androloc.schoolplaner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MenuMainActivity extends Activity {

	private final Context context = this;
	private DB_DatabaseHelper db;
	private String oldDatabaseName;
	private SharedPreferences calendar_Preferences;		//Kalender einstellungen
	private AdView adView; //AddMob 
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.menu_main);
	    
	    try {
	    	//StartUpActivity falls Settings nicht initialisiert
            if (ClassSettings.getActiveYearShow() == null) {
            	Intent i = new Intent(this,StartUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
		    String yearTitle = getString(R.string.app_name) + " " + ClassSettings.getActiveYearShow();
		    setTitle(yearTitle);
		} catch (Exception e) {
		}
	    
	    /* AddMob-Implementierung
	     * ---------------------- */
	    /*
		adView = new AdView(this, AdSize.BANNER, "a151b0535cc49ba");
	    LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
	    layout.addView(adView);
	    AdRequest adRequest = new AdRequest();
	    adRequest.addTestDevice(AdRequest.TEST_EMULATOR);      // Emulator
	    adRequest.addTestDevice("1C1D6798A3CA");               // Mein Huawei
	    adView.loadAd(adRequest);
		*/
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
	    /* -------------------------------
	     * Ende AddMob */
	    
	    
		//Datenbank initilisieren
	    db = new DB_DatabaseHelper(getApplicationContext());
	    oldDatabaseName = ClassSettings.getDatabaseName();

	    //On-Click für Kalender initialisieren
        //====================================
        ((ImageButton)findViewById(R.id.menuButton_Calendar)).setOnClickListener(calendarListener);
	    //On-Click für Hausaufgaben initialisieren
        //========================================
        ((ImageButton)findViewById(R.id.menuButton_Homework)).setOnClickListener(homeworkListener);
	    //On-Click für Notizen initialisieren
        //===================================
        ((ImageButton)findViewById(R.id.menuButton_Notes)).setOnClickListener(notesListener);
        //On-Click für Tests initialisieren
        //=================================
        ((ImageButton)findViewById(R.id.menuButton_Tests)).setOnClickListener(testsListener);
        //On-Click für Stundenplan-Verwaltung initialisieren
        //==================================================
        ((ImageButton)findViewById(R.id.menuButton_Plan)).setOnClickListener(planListener);
        //On-Click für Fächerverwaltung initialisieren
        //============================================
        ((ImageButton)findViewById(R.id.menuButton_Faecher)).setOnClickListener(faecherListener);
	    //On-Click für Lehrerverwaltung initialisieren
        //============================================
        ((ImageButton)findViewById(R.id.menuButton_Teacher)).setOnClickListener(teacherListener);
        //On-Click für Exit-Layout initialisieren
        //==========================================
        ((ImageButton)findViewById(R.id.menuButton_Exit)).setOnClickListener(exitListener);

	}

	/* AddMob deaktivieren
	 * ------------------- */
	@Override
	public void onDestroy() {
		adView.destroy();
		super.onDestroy();
	};
	
	///Titel ggf. aktualisieren
	protected void onResume() {
		super.onResume();
	    try {
	    	//StartUpActivity falls Settings nicht initialisiert
            if (ClassSettings.getActiveYearShow() == null) {
            	Intent i = new Intent(this,StartUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
		    String yearTitle = getString(R.string.app_name) + " " + ClassSettings.getActiveYearShow();
		    setTitle(yearTitle);
		} catch (Exception e) {
		}
	    //Prüfen ob ggf. das Schuljahr geändert wurde
	    if (!oldDatabaseName.equals(ClassSettings.getDatabaseName())) {
    	    db.close();
    	    db = null;
	    	oldDatabaseName = ClassSettings.getDatabaseName();
	    	ClassCalendar.reset();
	    	if (ClassCalendar.doesDatabaseExist(new ContextWrapper(getApplicationContext()), oldDatabaseName)) {
	    		//Datenbank existiert bereits und wird jetzt initialisiert
	    		db = new DB_DatabaseHelper(getApplicationContext());
	    	} else {
	    		//Datenbank für das geänderte Schuljahr existiert noch nicht
	    		//StartUp-Activity wird initialisiert
	    	 	calendar_Preferences = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor prefEdit = calendar_Preferences.edit();
		    	prefEdit.putBoolean("cal_created", false);
		    	prefEdit.commit();
		    	Intent i = new Intent(this,StartUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
	    	}
	    }

	};

	//On-Click-Event für Kalender
	//===========================
	OnClickListener calendarListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	 		Intent i = new Intent(MenuMainActivity.this, CalendarTabActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
	};

	//On-Click-Event für Hausaufgaben
	//===============================
	OnClickListener homeworkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	 		Intent i = new Intent(MenuMainActivity.this, HomeworkAddActivity.class);
	 		i.putExtra("index", 0);
	 		startActivity(i);
		}
	};

	//On-Click-Event für Notizen
	//==========================
	OnClickListener notesListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	 		Intent i = new Intent(MenuMainActivity.this, NotesAddActivity.class);
	 		i.putExtra("index", 0);
			startActivity(i);
		}
	};

	//On-Click-Event für Tests
	//========================
	OnClickListener testsListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	 		Intent i = new Intent(MenuMainActivity.this, TestsAddActivity.class);
	 		i.putExtra("index", 0);
			startActivity(i);
		}
	};

	//On-Click-Event für Stundenplan
	//==============================
	OnClickListener planListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	 		Intent i = new Intent(MenuMainActivity.this, PlanListActivity.class);
			startActivity(i);
		}
	};

	//On-Click-Event für Fächerverwaltung
	//===================================
	OnClickListener faecherListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	 		Intent i = new Intent(MenuMainActivity.this, FaecherListActivity.class);
			startActivity(i);
		}
	};

	//On-Click-Event für Lehrerverwaltung
	//===================================
	OnClickListener teacherListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	 		Intent i = new Intent(MenuMainActivity.this, TeacherListActivity.class);
			startActivity(i);
		}
	};
	
	//On-Click-Event für Exit
	//========================
	OnClickListener exitListener = new OnClickListener() {
	    public void onClick(View v) {
        	//Dialog mit Abfrage zum beenden
        	//==============================
			//AlertDialog-Builder referenzieren
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			//AlertDialog-Builder konfigurieren
			builder.setTitle(R.string.msg_title_finish);         										
			builder.setCancelable(true);											
			//OK/Ja Button
			builder.setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() { 
				@Override
				public void onClick(DialogInterface dialog, int which) {
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
	};

	//Methode muss zum Erstellen des Menüs überschrieben werden
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {   
    	MenuInflater inflater = getMenuInflater();   
        inflater.inflate(R.menu.main_menu, menu);   
        return super.onCreateOptionsMenu(menu);   
    }   

    //Methode muss zum reagieren auf das Anklicken der Menü-Items überschrieben werden
    @Override  
    public boolean onOptionsItemSelected(MenuItem item) {
       	Intent i;
    	switch (item.getItemId()) {
        case R.id.menu_main_settings:
        	//settings aufrufen
	 		i = new Intent(MenuMainActivity.this, CalendarSettingsActivity.class);
			startActivity(i);
        	return true;
        case R.id.menu_main_db:
			// Kalender-Lösch-Activity
	 		i = new Intent(MenuMainActivity.this, CalendarDeleteActivity.class);
			startActivity(i);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
