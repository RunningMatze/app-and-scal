package de.androloc.schoolplaner;

import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;

public class ClassPlan {

	/* Private Objekte */
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	private Context context;
	
	/* Member-Variablen */
	private long id;
	private String tag;
	private int stunde;
	private String beginn;
	private String ende;
	private long fach;
	private String raum;
	private String bemerkung;
	
	private int begin_hour;
	private int begin_minute;
	private int end_hour;
	private int end_minute;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public int getStunde() {
		return stunde;
	}
	public void setStunde(int stunde) {
		this.stunde = stunde;
	}
	
	public String getBeginn() {
		return beginn;
	}
	public void setBeginn(String beginn) {
		this.beginn = beginn;
	}
	
	public String getEnde() {
		return ende;
	}
	public void setEnde(String ende) {
		this.ende = ende;
	}
	
	public long getFach() {
		return fach;
	}
	public void setFach(long fach) {
		this.fach = fach;
	}
	
	public String getRaum() {
		return raum;
	}
	public void setRaum(String raum) {
		this.raum = raum;
	}
	
	public String getBemerkung() {
		return bemerkung;
	}
	public void setBemerkung(String bemerkung) {
		this.bemerkung = bemerkung;
	}

	public int getBegin_hour() {
		return begin_hour;
	}
	public void setBegin_hour(int begin_hour) {
		this.begin_hour = begin_hour;
	}
	
	public int getBegin_minute() {
		return begin_minute;
	}
	public void setBegin_minute(int begin_minute) {
		this.begin_minute = begin_minute;
	}
	
	public int getEnd_hour() {
		return end_hour;
	}
	public void setEnd_hour(int end_hour) {
		this.end_hour = end_hour;
	}
	
	public int getEnd_minute() {
		return end_minute;
	}
	public void setEnd_minute(int end_minute) {
		this.end_minute = end_minute;
	}

	/* Konstruktor */
	public ClassPlan(Context context) {
		super();
		this.context = context;
		id = 0;
		tag = "";
		stunde = 0;
		beginn = "";
		ende = "";
		fach = 0;
		raum = "";
		bemerkung = "";
		begin_hour = -1;
		begin_minute = -1;
		end_hour = -1;
		end_minute = -1;
	}

	/* Geänderten Fach-Datensatz in Datenbank speichern */
	public boolean updatePlan(long hourID) {
		db = new DB_DatabaseHelper();
		Hashtable<String,String> fields = new Hashtable<String,String>();
		fields.put(context.getString(R.string.field_tag), this.tag);
		fields.put(context.getString(R.string.field_stunde), Integer.toString(this.stunde));
		fields.put(context.getString(R.string.field_beginn), this.beginn);
		fields.put(context.getString(R.string.field_ende), this.ende);
		fields.put(context.getString(R.string.field_fach), Long.toString(this.fach));
		fields.put(context.getString(R.string.field_raum), this.raum);
		fields.put(context.getString(R.string.field_bemerkung), this.bemerkung);
		try {
			db.Update(context.getString(R.string.table_stundenplan), hourID ,fields);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			return false;
		}
		
	}

	/* Unterrichtsstunde über _id in der Datenbank abfragen */
	public boolean findHourByID(long id) {

		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_stundenplan), context.getString(R.string.field_id) + " = " + id, null);
        if (dbCursor.moveToFirst()) {
            this.tag = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_tag)));
            this.stunde = dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_stunde)));
            this.beginn = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_beginn)));
            this.ende = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_ende)));
            this.fach = dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_fach)));
            this.raum = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_raum)));
            this.bemerkung = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_bemerkung)));
            dbCursor.close();
            if (this.beginn != null && this.beginn.length() > 0) {
            	splitTimes();
            }
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

	/* nach der letzten erfassten gleichen Stunde eines anderen
	 * Tages suchen und diese Stunde als Default-Wert übergeben */
	public boolean searchDefaultTime(int stundeNr) {

		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_stundenplan), context.getString(R.string.field_fach) + ">0 AND "+ context.getString(R.string.field_stunde)+ "=" + Integer.toString(stundeNr) , null);
        if (dbCursor.getCount() > 0) {
            if (dbCursor.moveToLast()) {
                this.beginn = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_beginn)));
                this.ende = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_ende)));
                dbCursor.close();
                if (this.beginn !=null && this.beginn.length() > 0) {
                	splitTimes();
                    return true;
                }
                return false;
            } else {
                dbCursor.close();
        		return false;
            }
        } else {
        	return false;
        }
	}

	/* Cursor auf kompletten Plan zurückliefern */
	public Cursor getPlanCursor() {

		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_stundenplan), null, null);
        if (dbCursor.moveToFirst()) {
            return dbCursor;
        } else {
    		return null;
        }
	}

	/* Uhrzeiten in Stunden und Minuten zerlegen */
	public void splitTimes() {
		if (this.beginn != null && this.beginn.toString().length() > 0) {
			String[] temp;
			temp = this.beginn.split("\\:");
			if (temp.length == 2) {
				this.begin_hour = Integer.parseInt(temp[0]);
				this.begin_minute = Integer.parseInt(temp[1]);
			}
		}
		if (this.ende.length() > 0) {
			String[] temp;
			temp = this.ende.split("\\:");
			if (temp.length == 2) {
				this.end_hour = Integer.parseInt(temp[0]);
				this.end_minute = Integer.parseInt(temp[1]);
			}
		}
	}

	/* Uhrzeitanzeige aus Stunden und Minuten zusammensetzen */
	public void joinTimes() {
		String hh, mm;
		if (this.begin_hour >= 0) {
			hh = Integer.toString(this.begin_hour);
			mm = Integer.toString(this.begin_minute);
			if (hh.length() == 1) hh = "0" + hh;
			if (mm.length() == 1) mm = "0" + mm;
			this.beginn = hh + ":" + mm;
			hh = Integer.toString(this.end_hour);
			mm = Integer.toString(this.end_minute);
			if (hh.length() == 1) hh = "0" + hh;
			if (mm.length() == 1) mm = "0" + mm;
			this.ende = hh + ":" + mm;
		} else {
			this.beginn = "";
			this.ende = "";
		}
	}

}
