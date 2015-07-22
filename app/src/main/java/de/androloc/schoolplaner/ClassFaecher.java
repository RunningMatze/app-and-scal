package de.androloc.schoolplaner;

import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;

public class ClassFaecher {

	/* Private Objekte */
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	private Context context;

	/* Member-Variablen */
	private long id;
	private String fach;
	private String kurzcode;
	private long lehrer;
	private String bemerkung;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getFach() {
		return fach;
	}
	public void setFach(String fach) {
		this.fach = fach;
	}
	
	public String getKurzcode() {
		return kurzcode;
	}
	public void setKurzcode(String kurzcode) {
		this.kurzcode = kurzcode;
	}

	public long getLehrer() {
		return lehrer;
	}
	public void setLehrer(long lehrer) {
		this.lehrer = lehrer;
	}
	
	public String getBemerkung() {
		return bemerkung;
	}
	public void setBemerkung(String bemerkung) {
		this.bemerkung = bemerkung;
	}

	/* Konstruktor */
	public ClassFaecher(Context context) {
		super();
		this.context = context;
		id = 0;
		fach = "";
		kurzcode = "";
		lehrer = 0;
		bemerkung = "";
	}

	/* Fach über _id in der Datenbank abfragen */
	public boolean findFachByID(long id) {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_faecher), context.getString(R.string.field_id) + " = " + id, null);
        if (dbCursor.moveToFirst()) {
            this.fach = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_fach)));
            this.kurzcode = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_kurzcode)));
            this.lehrer = dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_teacher)));
            this.bemerkung = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_bemerkung)));
            dbCursor.close();
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

	/* Neuen Fach-Datensatz in Datenbank speichern */
	public boolean addFach() {
		db = new DB_DatabaseHelper();
		Hashtable<String,String> fields = new Hashtable<String,String>();
		fields.put(context.getString(R.string.field_fach), this.fach);
		fields.put(context.getString(R.string.field_kurzcode), this.kurzcode);
		fields.put(context.getString(R.string.field_teacher), Long.toString(this.lehrer));
		fields.put(context.getString(R.string.field_bemerkung), this.bemerkung);
		try {
			db.Add(context.getString(R.string.table_faecher),fields);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			return false;
		}
		
	}

	/* Geänderten Fach-Datensatz in Datenbank speichern */
	public boolean updateFach(long fachID) {
		db = new DB_DatabaseHelper();
		Hashtable<String,String> fields = new Hashtable<String,String>();
		fields.put(context.getString(R.string.field_fach), this.fach);
		fields.put(context.getString(R.string.field_kurzcode), this.kurzcode);
		fields.put(context.getString(R.string.field_teacher), Long.toString(this.lehrer));
		fields.put(context.getString(R.string.field_bemerkung), this.bemerkung);
		try {
			db.Update(context.getString(R.string.table_faecher), fachID ,fields);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			return false;
		}
		
	}

}
