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

public class FaecherAddActivity extends Activity {

	private Button buttonOK;
	private Button buttonNoTeacher;
	private Button buttonBack;
	private EditText editFach;
	private EditText editKurzcode;
	private EditText editBemerkung;
	private TextView tvTeacher;
	private long teacherID = 0;
	private ListView teacherListe;
	private TeacherSearchAdapter teacherListAdapter;
	private ListView faecherListe;
	private FaecherSearchAdapter faecherListAdapter;
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.faecher_add);

	    String yearTitle = getTitle() + " " + ClassSettings.getActiveYearShow();
	    setTitle(yearTitle);

	    //On-Click für Übernehmen Button
        //------------------------------
        buttonOK = (Button)findViewById(R.id.button_faecher_add_ok);
	    buttonOK.setOnClickListener(buttonOkListener);

        //On-Click für Abbrechen Button
        //------------------------------
        ((Button)findViewById(R.id.button_faecher_add_cancel)).setOnClickListener(buttonCancelListener);

        //On-Click für Fachsuche Button
        //-----------------------------
        ((ImageButton)findViewById(R.id.button_fach_search)).setOnClickListener(buttonFachSearchListener);

        //On-Click für Lehrersuche Button
        //-------------------------------
        ((ImageButton)findViewById(R.id.button_teacher_search)).setOnClickListener(buttonTeacherSearchListener);

        editFach = (EditText)findViewById(R.id.edit_fach_bezeichnung);
        editFach.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				//wenn Vorname oder Name Text enthält dann OK frei sonst OK sperren
				CheckInput();
			}
		});
        editKurzcode = (EditText)findViewById(R.id.edit_fach_kurcode);
        editKurzcode.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				//wenn Vorname oder Name Text enthält dann OK frei sonst OK sperren
				CheckInput();
			}
		});
        tvTeacher = (TextView)findViewById(R.id.edit_fach_teacher);
        editBemerkung = (EditText)findViewById(R.id.edit_fach_bemerkungen);

	}

	//OnClick-Listener für Übernehmen-Button
	//--------------------------------------
	OnClickListener buttonOkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//Neuen Lehrer-Datensatz in Datenbank speichern
			//---------------------------------------------
			ClassFaecher newFach = new ClassFaecher(FaecherAddActivity.this);
			newFach.setFach(editFach.getText().toString());
			newFach.setKurzcode(editKurzcode.getText().toString());
			newFach.setLehrer(teacherID);
			newFach.setBemerkung(editBemerkung.getText().toString());
			newFach.addFach();
			finish();
		}
	};
	
	//OnClick-Listener für Fachsuche-Button
	//-------------------------------------
	OnClickListener buttonFachSearchListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final Dialog dialog = new Dialog(FaecherAddActivity.this, R.style.dialogtheme);
			dialog.setContentView(R.layout.faecher_search_list);
			dialog.setCancelable(true);
			dialog.setTitle(R.string.cd_faecher_search);
			faecherListe = (ListView) dialog.findViewById(R.id.list_search_fach);
			buttonBack = (Button) dialog.findViewById(R.id.button_cancel_fachsearch);
			db = new DB_DatabaseHelper();
	        //SQL-Abfrage durchführen Cursor holen
	        dbCursor = db.query(getString(R.string.table_fachkurzcode), null, getString(R.string.field_fach)+ " ASC");
	        startManagingCursor(dbCursor);
	        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
	        faecherListAdapter = new FaecherSearchAdapter(faecherListe.getContext(),dbCursor,true);
	        faecherListe.setAdapter(faecherListAdapter);
	        /*Lehrer wurde aus der Liste ausgewählt*/
	        faecherListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	@Override
		        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
	        		dialog.cancel();
	        		SetFachAndKurzcode(id);
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

	//OnClick-Listener für Lehrersuche-Button
	//---------------------------------------
	OnClickListener buttonTeacherSearchListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final Dialog dialog = new Dialog(FaecherAddActivity.this, R.style.dialogtheme);
			dialog.setContentView(R.layout.teacher_search_list);
			dialog.setCancelable(true);
			dialog.setTitle(R.string.cd_teacher_search);
			teacherListe = (ListView) dialog.findViewById(R.id.list_search_teacher);
			buttonNoTeacher = (Button) dialog.findViewById(R.id.button_no_teacher);
			db = new DB_DatabaseHelper();
	        //SQL-Abfrage durchführen Cursor holen
	        dbCursor = db.query(getString(R.string.table_lehrer), null, getString(R.string.field_nachname)+ " ASC");
	        startManagingCursor(dbCursor);
	        //CursorAdapter zum befüllen der Liste initialisieren und an Liste übergeben
	        teacherListAdapter = new TeacherSearchAdapter(teacherListe.getContext(),dbCursor,true);
	        teacherListe.setAdapter(teacherListAdapter);
	        /*Lehrer wurde aus der Liste ausgewählt*/
	        teacherListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	@Override
		        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
	        		dialog.cancel();
	        		teacherID = id;
	        		SetTeacherName(teacherID);
	        	}
			});
	        /*Keinen Lehrer zuordnen wurde ausgewählt*/
	        buttonNoTeacher.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
					teacherID = 0;
	        		SetTeacherName(teacherID);
				}
			});
	        /*Suchen-Dialog anzeigen*/
	        dialog.show();
		}

	};
	
	//OnClick-Listener für Abbrechen-Button
	//-------------------------------------
	OnClickListener buttonCancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Activity beenden
			finish();
		}
	};

	//Lehrernamen anhand der Lehrer-ID anzeigen
	//-----------------------------------------
	private void SetTeacherName(long teacherID) {
		if (teacherID == 0) {
			tvTeacher.setText(R.string.tv_faecher_noteacher);
		} else {
			ClassTeacher actTeacher = new ClassTeacher(this);
			actTeacher.FindTeacherByID(teacherID);
			tvTeacher.setText(actTeacher.BuildTeacherName());
		}
	}

	//Fach und Kurzcode an die Eingabefelder übergeben
	//------------------------------------------------
	private void SetFachAndKurzcode(long fachID) {
		ClassFachKurzcode actFachKurz = new ClassFachKurzcode(this);
		actFachKurz.FindFachByID(fachID);
		editFach.setText(actFachKurz.getFach());
		editKurzcode.setText(actFachKurz.getKurzcode());
	}
	
	//Funktion zum Sperren/Freigeben des OK-Buttons
	//---------------------------------------------
	private void CheckInput() {
		String fach = editFach.getText().toString().trim();
		String kuerzel = editKurzcode.getText().toString().trim();
		if ((fach.length() == 0) || (kuerzel.length() == 0)) {
			buttonOK.setEnabled(false);
		} else {
			buttonOK.setEnabled(true);
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
	 		Intent i = new Intent(FaecherAddActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        	finish();
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
