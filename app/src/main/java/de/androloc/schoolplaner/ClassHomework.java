package de.androloc.schoolplaner;

import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;

public class ClassHomework {

	/* Private Objekte */
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	private Context context;

	/* Member-Variablen */
	private long id;
	private long fach;
	private long erledigenAm;
	private long abgabeAm;
	private String aufgaben;
	private int erledigt;

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
	
	public long getErledigenAm() {
		return erledigenAm;
	}
	public void setErledigenAm(long erledigenAm) {
		this.erledigenAm = erledigenAm;
	}
	
	public long getAbgabeAm() {
		return abgabeAm;
	}
	public void setAbgabeAm(long abgabeAm) {
		this.abgabeAm = abgabeAm;
	}
	
	public String getAufgaben() {
		return aufgaben;
	}
	public void setAufgaben(String aufgaben) {
		this.aufgaben = aufgaben;
	}
	
	public int getErledigt() {
		return erledigt;
	}
	public void setErledigt(int erledigt) {
		this.erledigt = erledigt;
	}

	/* Konstruktor */
	public ClassHomework(Context context) {
		super();
		this.context = context;
		id = 0;
		fach = 0;
		erledigenAm=0;
		abgabeAm=0;
		aufgaben = "";
		erledigt=0;
	}
	
	/* Hausaufgabe über _id in der Datenbank abfragen */
	public boolean findHomeworkByID(long id) {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_hausaufgaben), context.getString(R.string.field_id) + " = " + id, null);
        if (dbCursor.moveToFirst()) {
            this.fach = dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_fach)));
            this.erledigenAm = dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_erledigen_am)));
            this.abgabeAm = dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_abgabe_am)));
            this.aufgaben = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_aufgaben)));
            this.erledigt = dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_erledigt)));
            dbCursor.close();
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

	/* Neuen Hausaufgaben-Datensatz in Datenbank speichern */
	public boolean addHomework() {
		db = new DB_DatabaseHelper();
		Hashtable<String,String> fields = new Hashtable<String,String>();
		fields.put(context.getString(R.string.field_fach), Long.toString(this.fach));
		fields.put(context.getString(R.string.field_erledigen_am), Long.toString(this.erledigenAm));
		fields.put(context.getString(R.string.field_abgabe_am), Long.toString(this.abgabeAm));
		fields.put(context.getString(R.string.field_aufgaben), this.aufgaben);
		fields.put(context.getString(R.string.field_erledigt), Integer.toString(this.erledigt));
		try {
			db.Add(context.getString(R.string.table_hausaufgaben),fields);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			return false;
		}
	}

	/* Geänderten Hausaufgaben-Datensatz in Datenbank speichern */
	public boolean updateHomework(long homeworkID) {
		db = new DB_DatabaseHelper();
		Hashtable<String,String> fields = new Hashtable<String,String>();
		fields.put(context.getString(R.string.field_fach), Long.toString(this.fach));
		fields.put(context.getString(R.string.field_erledigen_am), Long.toString(this.erledigenAm));
		fields.put(context.getString(R.string.field_abgabe_am), Long.toString(this.abgabeAm));
		fields.put(context.getString(R.string.field_aufgaben), this.aufgaben);
		fields.put(context.getString(R.string.field_erledigt), Integer.toString(this.erledigt));
		try {
			db.Update(context.getString(R.string.table_hausaufgaben), homeworkID ,fields);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			return false;
		}
	}
	
	/* Prüfen ob zue inem Tag Hausaufgaben eingegeben wurden */
	public boolean dayHasHomework(long dayID, int hwType) {
		db = new DB_DatabaseHelper();
        /* hwType 0 = Beides (erledigen und abgeben)
         * hwType 1 = nur erledigen
         * hwType 2 = nur abgeben 
         * hwType 3 = Beides (erledigen (nur wenn noch nicht erledigt) und abgeben) */
		switch (hwType) {
		case 0:
			dbCursor=db.query(context.getString(R.string.table_hausaufgaben), context.getString(R.string.field_erledigen_am) + " = " + dayID + " OR " + context.getString(R.string.field_abgabe_am) + " = " + dayID, null);
			break;
		case 1:
			dbCursor=db.query(context.getString(R.string.table_hausaufgaben), context.getString(R.string.field_erledigen_am) + " = " + dayID, null);
			break;
		case 2:
			dbCursor=db.query(context.getString(R.string.table_hausaufgaben), context.getString(R.string.field_abgabe_am) + " = " + dayID, null);
			break;
		case 3:
			dbCursor=db.query(context.getString(R.string.table_hausaufgaben), "(" + context.getString(R.string.field_erledigen_am) + " = " + dayID + " AND "+ context.getString(R.string.field_erledigt)+" = 0)" + " OR " + context.getString(R.string.field_abgabe_am) + " = " + dayID, null);
			break;
		}
        if (dbCursor.getCount() > 0) {
            dbCursor.close();
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

}
