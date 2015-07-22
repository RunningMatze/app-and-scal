package de.androloc.schoolplaner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class TeacherEditActivity extends Activity {

	private Button buttonOK;
	private EditText editVorname;
	private EditText editName;
	private EditText editKuerzel;
	private EditText editTelefon;
	private EditText editEMail;
	private EditText editBemerkung;
	private RadioGroup anredeAuswahl;
	private RadioButton anredeHerr;
	private RadioButton anredeFrau;
	private long teacherID;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    String yearTitle = getTitle() + " " + ClassSettings.getActiveYearShow();
	    setTitle(yearTitle);
	    
	    setContentView(R.layout.teacher_add);
		
	    /* Andere Überschrift setzen */
	    ((TextView)findViewById(R.id.teacherAddHeader)).setText(getString(R.string.tv_teacher_edit));

	    //Index des Datensatzes
	    //---------------------
	    teacherID = getIntent().getLongExtra("index", 0);
	    if (teacherID == 0) {
	    	finish();
	    }
	    
        //On-Click für Übernehmen Button
        //------------------------------
	    buttonOK = (Button)findViewById(R.id.button_teacher_add_ok);
	    buttonOK.setOnClickListener(buttonOkListener);
	    buttonOK.setEnabled(true);
	    
        //On-Click für Abbrechen Button
        //------------------------------
        ((Button)findViewById(R.id.button_teacher_add_cancel)).setOnClickListener(buttonCancelListener);

        //Anrede-Auswahl
        //--------------
        anredeAuswahl = (RadioGroup)findViewById(R.id.opt_group_anrede);
        anredeHerr = (RadioButton)findViewById(R.id.opt_teacher_herr);
        anredeFrau = (RadioButton)findViewById(R.id.opt_teacher_frau);
        
        // Eingabefelder
        // -------------
        editVorname = (EditText)findViewById(R.id.edit_teacher_vorname);
        editName = (EditText)findViewById(R.id.edit_teacher_name);
        editKuerzel = (EditText)findViewById(R.id.edit_teacher_kuerzel);
        editTelefon = (EditText)findViewById(R.id.edit_teacher_telefon);
        editEMail = (EditText)findViewById(R.id.edit_teacher_email);
        editBemerkung = (EditText)findViewById(R.id.edit_teacher_bemerkungen);
	
        //Lehrerdatensatz auslesen und Daten an widgets übergeben
        //-------------------------------------------------------
        ClassTeacher teacher = new ClassTeacher(this);
        if (teacher.FindTeacherByID(teacherID) == true) {
        	if (teacher.getAnrede().equals("Herr")) {
        		anredeHerr.setChecked(true);
        	} else {
        		anredeFrau.setChecked(true);
        	}
            editVorname.setText(teacher.getVorname());
            editName.setText(teacher.getNachname());
            editKuerzel.setText(teacher.getKuerzel());
            editTelefon.setText(teacher.getTelefon());
            editEMail.setText(teacher.getEmail());
            editBemerkung.setText(teacher.getBemerkung());
        } else {
        	finish();
        }
	}

	//OnClick-Listener für Übernehmen-Button
	//--------------------------------------
	OnClickListener buttonOkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//Neuen Lehrer-Datensatz in Datenbank speichern
			//---------------------------------------------
			ClassTeacher editTeacher = new ClassTeacher(TeacherEditActivity.this);
			int anredeID = anredeAuswahl.getCheckedRadioButtonId();
			if (anredeID == R.id.opt_teacher_herr) {
			    editTeacher.setAnrede("Herr");
			} else {
			    editTeacher.setAnrede("Frau");
			}
			editTeacher.setVorname(editVorname.getText().toString());
			editTeacher.setNachname(editName.getText().toString());
			editTeacher.setKuerzel(editKuerzel.getText().toString());
			editTeacher.setTelefon(editTelefon.getText().toString());
			editTeacher.setEmail(editEMail.getText().toString());
			editTeacher.setBemerkung(editBemerkung.getText().toString());
			editTeacher.UpdateTeacher(teacherID);
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
	 		Intent i = new Intent(TeacherEditActivity.this, MenuMainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        	finish();
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
