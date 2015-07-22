package de.androloc.schoolplaner;

import android.content.Context;
import android.database.Cursor;

public class ClassFeiertage {

	/* Private Objekte */
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	private Context context;

	/* Member-Variablen */
	private long id;
	private String dateSort;
	private String dateShow;
	private String bezeichnung;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getDateSort() {
		return dateSort;
	}
	public void setDateSort(String dateSort) {
		this.dateSort = dateSort;
	}
	
	public String getDateShow() {
		return dateShow;
	}
	public void setDateShow(String dateShow) {
		this.dateShow = dateShow;
	}
	
	public String getBezeichnung() {
		return bezeichnung;
	}
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	/* Konstruktor */
	public ClassFeiertage(Context context) {
		super();
		this.context = context;
		id = 0;
		dateSort = "";
		dateShow = "";
		bezeichnung = "";
	}

	/* Feiertag über _id in der Datenbank abfragen */
	public boolean findFeiertagByID(long id) {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_feiertage), context.getString(R.string.field_id) + " = " + id, null);
        if (dbCursor.moveToFirst()) {
            this.dateSort = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_sort)));
            this.dateShow = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_show)));
            this.bezeichnung = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_bezeichnung)));
            dbCursor.close();
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

}
