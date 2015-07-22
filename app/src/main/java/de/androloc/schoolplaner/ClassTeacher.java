package de.androloc.schoolplaner;

import java.util.Hashtable;
import android.content.Context;
import android.database.Cursor;

public class ClassTeacher {

	/* Private Objekte */
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	private Context context;
	
	/* Member-Variablen */
	private long id;
	private String anrede;
	private String vorname;
	private String nachname;
	private String kuerzel;
	private String telefon;
	private String email;
	private String bemerkung;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getAnrede() {
		return anrede;
	}
	public void setAnrede(String anrede) {
		this.anrede = anrede;
	}
	
	public String getVorname() {
		return vorname;
	}
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	
	public String getNachname() {
		return nachname;
	}
	public void setNachname(String nachname) {
		this.nachname = nachname;
	}
	
	public String getKuerzel() {
		return kuerzel;
	}
	public void setKuerzel(String kuerzel) {
		this.kuerzel = kuerzel;
	}
	
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getBemerkung() {
		return bemerkung;
	}
	public void setBemerkung(String bemerkung) {
		this.bemerkung = bemerkung;
	}
	
	public ClassTeacher(Context context) {
		super();
		this.context = context;
		id = 0;
		anrede = "";
		vorname = "";
		nachname = "";
		kuerzel = "";
		telefon = "";
		email = "";
		bemerkung = "";
	}
	
	/* Teacher über _id in der Datenbank abfragen */
	public boolean FindTeacherByID(long id) {

		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_lehrer), context.getString(R.string.field_id) + " = " + id, null);
        if (dbCursor.moveToFirst()) {
            this.anrede = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_anrede)));
            this.vorname = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_vorname)));
            this.nachname = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_nachname)));
            this.kuerzel = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_kuerzel)));
            this.telefon = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_telefon)));
            this.email = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_email)));
            this.bemerkung = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_bemerkung)));
            dbCursor.close();
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

	/* Neuen Lehrer-Datensatz in Datenbank speichern */
	public boolean AddTeacher() {
		db = new DB_DatabaseHelper();
		Hashtable<String,String> fields = new Hashtable<String,String>();
		fields.put(context.getString(R.string.field_anrede), this.anrede);
		fields.put(context.getString(R.string.field_vorname), this.vorname);
		fields.put(context.getString(R.string.field_nachname), this.nachname);
		fields.put(context.getString(R.string.field_kuerzel), this.kuerzel);
		fields.put(context.getString(R.string.field_telefon), this.telefon);
		fields.put(context.getString(R.string.field_email), this.email);
		fields.put(context.getString(R.string.field_bemerkung), this.bemerkung);
		try {
			db.Add(context.getString(R.string.table_lehrer),fields);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			return false;
		}
	}

	/* Update eines geänderten Lehrer-Datensatzes */
	public boolean UpdateTeacher(long teacherID) {
		db = new DB_DatabaseHelper();
		Hashtable<String,String> fields = new Hashtable<String,String>();
		fields.put(context.getString(R.string.field_anrede), this.anrede);
		fields.put(context.getString(R.string.field_vorname), this.vorname);
		fields.put(context.getString(R.string.field_nachname), this.nachname);
		fields.put(context.getString(R.string.field_kuerzel), this.kuerzel);
		fields.put(context.getString(R.string.field_telefon), this.telefon);
		fields.put(context.getString(R.string.field_email), this.email);
		fields.put(context.getString(R.string.field_bemerkung), this.bemerkung);
		try {
			db.Update(context.getString(R.string.table_lehrer), teacherID, fields);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			return false;
		}
	}
	/* Anrede und Name zusammensetzen */
	public String BuildTeacherName() {

		String anzeigeName = "";
		anzeigeName = this.anrede;
		if (this.vorname.length() > 0) {
			anzeigeName = anzeigeName + " " + this.vorname;
		}
		if (this.nachname.length() > 0) {
			anzeigeName = anzeigeName + " " + this.nachname;
		}
		return anzeigeName;
	}
}
