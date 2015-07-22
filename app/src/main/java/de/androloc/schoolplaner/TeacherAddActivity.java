package de.androloc.schoolplaner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class TeacherAddActivity extends Activity {

	private EditText editVorname;
	private EditText editName;
	private EditText editKuerzel;
	private EditText editTelefon;
	private EditText editEMail;
	private EditText editBemerkung;
	private Button buttonOK;
	private RadioGroup anredeAuswahl;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.teacher_add);
	
	    String yearTitle = getTitle() + " " + ClassSettings.getActiveYearShow();
	    setTitle(yearTitle);
	    
	    //On-Click für Übernehmen Button
        //------------------------------
        buttonOK = (Button)findViewById(R.id.button_teacher_add_ok);
	    buttonOK.setOnClickListener(buttonOkListener);

        //On-Click für Abbrechen Button
        //------------------------------
        ((Button)findViewById(R.id.button_teacher_add_cancel)).setOnClickListener(buttonCancelListener);

        //Anrede-Auswahl
        //--------------
        anredeAuswahl = (RadioGroup)findViewById(R.id.opt_group_anrede);
        
        /* Eingabefelder für Vorname und Name zur Prüfung ob
         * Eingaben gemacht wurden und ob der OK-Button freigegeben
         * werden kann.
         * --------------------------------------------------------
         */
        editVorname = (EditText)findViewById(R.id.edit_teacher_vorname);
        editVorname.addTextChangedListener(new TextWatcher() {
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
        editName = (EditText)findViewById(R.id.edit_teacher_name);
        editName.addTextChangedListener(new TextWatcher() {
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
        editKuerzel = (EditText)findViewById(R.id.edit_teacher_kuerzel);
        editTelefon = (EditText)findViewById(R.id.edit_teacher_telefon);
        editEMail = (EditText)findViewById(R.id.edit_teacher_email);
        editBemerkung = (EditText)findViewById(R.id.edit_teacher_bemerkungen);
	}

	//OnClick-Listener für Übernehmen-Button
	//--------------------------------------
	OnClickListener buttonOkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//Neuen Lehrer-Datensatz in Datenbank speichern
			//---------------------------------------------
			ClassTeacher teacher = new ClassTeacher(TeacherAddActivity.this);
			int anredeID = anredeAuswahl.getCheckedRadioButtonId();
			if (anredeID == R.id.opt_teacher_herr) {
				teacher.setAnrede("Herr");
			} else {
				teacher.setAnrede("Frau");
			}
			teacher.setVorname(editVorname.getText().toString());
			teacher.setNachname(editName.getText().toString());
		    teacher.setKuerzel(editKuerzel.getText().toString());
			teacher.setTelefon(editTelefon.getText().toString());
			teacher.setEmail(editEMail.getText().toString());
			teacher.setBemerkung(editBemerkung.getText().toString());
			teacher.AddTeacher();
			finish();
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
	
	//Funktion zum Sperren/Freigeben des OK-Buttons
	//---------------------------------------------
	private void CheckInput() {
		String vName = editVorname.getText().toString().trim();
		String nName = editName.getText().toString().trim();
		if ((vName.length() == 0) && (nName.length() == 0)) {
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
	 		Intent i = new Intent(TeacherAddActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        	finish();
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
