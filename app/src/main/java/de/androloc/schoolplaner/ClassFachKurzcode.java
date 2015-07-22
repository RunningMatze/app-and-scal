package de.androloc.schoolplaner;

import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;

public class ClassFachKurzcode {

	/* Private Objekte */
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	private Context context;

	/* Member-Variablen */
	private long id;
	private String fach;
	private String kurzcode;
	
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

	/* Konstruktor */
	public ClassFachKurzcode(Context context) {
		super();
		this.context = context;
		id = 0;
		fach = "";
		kurzcode = "";
	}

	/* Fach über _id in der Datenbank abfragen */
	public boolean FindFachByID(long id) {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_fachkurzcode), context.getString(R.string.field_id) + " = " + id, null);
        if (dbCursor.moveToFirst()) {
            this.fach = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_fach)));
            this.kurzcode = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_kurzcode)));
            dbCursor.close();
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

	/* Neuen Fach-Datensatz in Datenbank speichern */
	public boolean AddFach() {
		db = new DB_DatabaseHelper();
		Hashtable<String,String> fields = new Hashtable<String,String>();
		fields.put(context.getString(R.string.field_fach), this.fach);
		fields.put(context.getString(R.string.field_kurzcode), this.kurzcode);
		try {
			db.Add(context.getString(R.string.table_fachkurzcode),fields);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			return false;
		}
		
	}

}
