package de.androloc.schoolplaner;

import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;

public class ClassTests {

	/* Private Objekte */
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	private Context context;

	/* Member-Variablen */
	private long id;
	private long fach;
	private String testArt;
	private long termin;
	private String beschreibung;
	private String note;
	private String durchschnitt;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public long getFach() {
		return fach;
	}
	public void setFach(long fach) {
		this.fach = fach;
	}
	
	public String gettestArt() {
		return testArt;
	}
	public void settestArt(String testArt) {
		this.testArt = testArt;
	}
	
	public long getTermin() {
		return termin;
	}
	public void setTermin(long termin) {
		this.termin = termin;
	}
	
	public String getBeschreibung() {
		return beschreibung;
	}
	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}
	
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	public String getDurchschnitt() {
		return durchschnitt;
	}
	public void setDurchschnitt(String durchschnitt) {
		this.durchschnitt = durchschnitt;
	}

	/* Konstruktor */
	public ClassTests(Context context) {
		super();
		this.context = context;
		id = 0;
		fach = 0;
		termin=0;
		testArt = "";
		beschreibung = "";
		note = "";
		durchschnitt = "";
	}

	/* Test über _id in der Datenbank abfragen */
	public boolean findTestByID(long id) {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_tests), context.getString(R.string.field_id) + " = " + id, null);
        if (dbCursor.moveToFirst()) {
            this.fach = dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_fach)));
            this.testArt = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_art)));
            this.termin = dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_kalendertag)));
            this.beschreibung = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_aufgaben)));
            this.note = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_note)));
            this.durchschnitt = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_durchschnitt)));
            dbCursor.close();
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

	/* Neuen Test-Datensatz in Datenbank speichern */
	public boolean addTest() {
		db = new DB_DatabaseHelper();
		Hashtable<String,String> fields = new Hashtable<String,String>();
		fields.put(context.getString(R.string.field_fach), Long.toString(this.fach));
		fields.put(context.getString(R.string.field_art), this.testArt);
		fields.put(context.getString(R.string.field_kalendertag), Long.toString(this.termin));
		fields.put(context.getString(R.string.field_aufgaben), this.beschreibung);
		fields.put(context.getString(R.string.field_note), this.note);
		fields.put(context.getString(R.string.field_durchschnitt), this.durchschnitt);
		try {
			db.Add(context.getString(R.string.table_tests),fields);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			return false;
		}
	}

	/* Geänderten Hausaufgaben-Datensatz in Datenbank speichern */
	public boolean updateTest(long testID) {
		db = new DB_DatabaseHelper();
		Hashtable<String,String> fields = new Hashtable<String,String>();
		fields.put(context.getString(R.string.field_fach), Long.toString(this.fach));
		fields.put(context.getString(R.string.field_art), this.testArt);
		fields.put(context.getString(R.string.field_kalendertag), Long.toString(this.termin));
		fields.put(context.getString(R.string.field_aufgaben), this.beschreibung);
		fields.put(context.getString(R.string.field_note), this.note);
		fields.put(context.getString(R.string.field_durchschnitt), this.durchschnitt);
		try {
			db.Update(context.getString(R.string.table_tests), testID ,fields);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			return false;
		}
	}

	/* Prüfen ob zu einem Tag Tests eingegeben wurden */
	public boolean dayHasTests(long dayID) {
		db = new DB_DatabaseHelper();
		dbCursor=db.query(context.getString(R.string.table_tests), context.getString(R.string.field_kalendertag) + " = " + dayID, null);
        if (dbCursor.getCount() > 0) {
            dbCursor.close();
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

}
