package de.androloc.schoolplaner;

import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;

public class ClassNotes {

	/* Private Objekte */
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	private Context context;

	/* Member-Variablen */
	private long id;
	private long datum;
	private String notiz;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public long getDatum() {
		return datum;
	}
	public void setDatum(long datum) {
		this.datum = datum;
	}
	
	public String getNotiz() {
		return notiz;
	}
	public void setNotiz(String notiz) {
		this.notiz = notiz;
	}

	/* Konstruktor */
	public ClassNotes(Context context) {
		super();
		this.context = context;
		id = 0;
		datum=0;
		notiz = "";
	}

	/* Notiz über _id in der Datenbank abfragen */
	public boolean findNoteByID(long id) {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_notizen), context.getString(R.string.field_id) + " = " + id, null);
        if (dbCursor.moveToFirst()) {
            this.datum = dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_kalendertag)));
            this.notiz = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_notiz)));
            dbCursor.close();
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

	/* Neuen Test-Datensatz in Datenbank speichern */
	public boolean addNote() {
		db = new DB_DatabaseHelper();
		Hashtable<String,String> fields = new Hashtable<String,String>();
		fields.put(context.getString(R.string.field_kalendertag), Long.toString(this.datum));
		fields.put(context.getString(R.string.field_notiz), this.notiz);
		try {
			db.Add(context.getString(R.string.table_notizen),fields);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			return false;
		}
	}

	/* Geänderten Notiz-Datensatz in Datenbank speichern */
	public boolean updateNote(long testID) {
		db = new DB_DatabaseHelper();
		Hashtable<String,String> fields = new Hashtable<String,String>();
		fields.put(context.getString(R.string.field_kalendertag), Long.toString(this.datum));
		fields.put(context.getString(R.string.field_notiz), this.notiz);
		try {
			db.Update(context.getString(R.string.table_notizen), testID ,fields);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			return false;
		}
	}

	/* Prüfen ob zu einem Tag eine Notiz eingegeben wurden */
	public boolean dayHasNote(long dayID) {
		db = new DB_DatabaseHelper();
		dbCursor=db.query(context.getString(R.string.table_notizen), context.getString(R.string.field_kalendertag) + " = " + dayID, null);
        if (dbCursor.getCount() > 0) {
            dbCursor.close();
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

}
