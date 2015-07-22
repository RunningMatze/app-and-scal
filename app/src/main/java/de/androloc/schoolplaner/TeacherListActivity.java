package de.androloc.schoolplaner;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class TeacherListActivity extends Activity {

	private ListView teacherListe;
	private TeacherListAdapter listAdapter;
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.teacher_list_act);

	    String yearTitle = getTitle() + " " + ClassSettings.getActiveYearShow();
	    setTitle(yearTitle);
	    
        //On-Click für Hinzufügen Button
        //------------------------------
        ((Button)findViewById(R.id.button_teacher_add)).setOnClickListener(teacherAddListener);
	
        /* Listview und Adapter initialisieren
         * ----------------------------------- */
        teacherListe = (ListView)findViewById(R.id.listViewTeacher);
        db = new DB_DatabaseHelper();
        //SQL-Abfrage durchführen Cursor holen
        dbCursor = db.query(getString(R.string.table_lehrer), null, null);
        startManagingCursor(dbCursor);
        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
        listAdapter = new TeacherListAdapter(teacherListe.getContext(),dbCursor,true);
        teacherListe.setAdapter(listAdapter);
	}

	//On-Click-Listener für Hinzufügen-Button
	//---------------------------------------
	OnClickListener teacherAddListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//Activity zur Erfassung eines neuen Lehrers starten
			Intent i = new Intent(TeacherListActivity.this, TeacherAddActivity.class);
			startActivity(i);
		}
	};

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
	 		Intent i = new Intent(TeacherListActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        	finish();
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
