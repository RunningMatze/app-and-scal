package de.androloc.schoolplaner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;

public class ClassCalendar {

	/* Private Objekte */
	private DB_DatabaseHelper db;
	private Cursor dbCursor;
	private Context context;
	private List<ClassCalendarDay> week, month;
	SimpleDateFormat sortFormat, searchFormat;

	/* Member-Variablen */
	private long id;
	private String datum_sort;
	private String datum_show;
	private String wochentag;
	private int kw;
	private int feiertag;
	private int ferientag;

	/* Statische Membervariablen */
	private static ClassCalendarDay firstDay = null;
	private static ClassCalendarDay lastDay = null;
	private static ClassCalendarDay today = null;
	private static int activeView;
	private static long activeDayID;
	private static int activeHomeworkView;
	private static int firstMonth;
	private static int firstYear;
	private static int lastMonth;
	private static int lastYear;
	
	//Prüfen ob die Datenbank für ein Schuljahr bereits existiert
	public static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
	    File dbFile=context.getDatabasePath(dbName);
	    return dbFile.exists();
	}
	
	public static void reset() {
		firstDay = null;
		lastDay = null;
		today = null;
		activeView = 0;
		activeDayID = 0;
		activeHomeworkView = 0;
		firstMonth = 0;
		firstYear = 0;
		lastMonth = 0;
		lastYear = 0;
	}

	public static int getFirstMonth() {
		return firstMonth;
	}
	public static void setFirstMonth(int firstMonth) {
		ClassCalendar.firstMonth = firstMonth;
	}

	public static int getFirstYear() {
		return firstYear;
	}
	public static void setFirstYear(int firstYear) {
		ClassCalendar.firstYear = firstYear;
	}

	public static int getLastMonth() {
		return lastMonth;
	}
	public static void setLastMonth(int lastMonth) {
		ClassCalendar.lastMonth = lastMonth;
	}

	public static int getLastYear() {
		return lastYear;
	}
	public static void setLastYear(int lastYear) {
		ClassCalendar.lastYear = lastYear;
	}

	public static ClassCalendarDay getFirstDay() {
		return firstDay;
	}

	public static ClassCalendarDay getLastDay() {
		return lastDay;
	}
	
	public static ClassCalendarDay getToday() {
		return today;
	}

	public static int getActiveView() {
		return activeView;
	}
	public static void setActiveView(int activeView) {
		ClassCalendar.activeView = activeView;
	}
	
	public static int getActiveHomeworkView() {
		return activeHomeworkView;
	}
	public static void setActiveHomeworkView(int activeHomeworkView) {
		ClassCalendar.activeHomeworkView = activeHomeworkView;
	}

	public static long getActiveDayID() {
		return activeDayID;
	}
	public static void setActiveDayID(long activeDayID) {
		ClassCalendar.activeDayID = activeDayID;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getDatum_sort() {
		return datum_sort;
	}
	public void setDatum_sort(String datum_sort) {
		this.datum_sort = datum_sort;
	}

	public String getDatum_show() {
		return datum_show;
	}
	public void setDatum_show(String datum_show) {
		this.datum_show = datum_show;
	}

	public String getWochentag() {
		return wochentag;
	}
	public void setWochentag(String wochentag) {
		this.wochentag = wochentag;
	}

	public int getKw() {
		return kw;
	}
	public void setKw(int kw) {
		this.kw = kw;
	}

	public int getFeiertag() {
		return feiertag;
	}
	public void setFeiertag(int feiertag) {
		this.feiertag = feiertag;
	}

	public int getFerientag() {
		return ferientag;
	}
	public void setFerientag(int ferientag) {
		this.ferientag = ferientag;
	}

	/* Konstruktor */
	public ClassCalendar(Context context) {
		super();
		this.context = context;
		id = 0;
		datum_sort = "";
		datum_show = "";
		wochentag = "";
		kw = 0;
		ferientag = 0;
		feiertag = 0;
		if ((firstDay == null) || (lastDay == null)) {
			GetFirstLastDay();
		}
		if (today == null) {
			GetToday();
		}
	}

	/* Ersten und letzten Schultag ermitteln */
	private void GetFirstLastDay() {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_kalender), null, context.getString(R.string.field_datum_sort) + " ASC");
        if (dbCursor.moveToFirst()) {
    		firstDay = new ClassCalendarDay();
        	firstDay.setId(dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_id))));
        	firstDay.setDatum_sort(this.datum_sort = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_sort))));
        	firstDay.setDatum_show(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_show))));
        	firstDay.setWochentag(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_wochentag))));
        	firstDay.setKw(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_kw))));
        	firstDay.setFerientag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_ferientag))));
        	firstDay.setFeiertag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_feiertag))));
        	firstMonth = firstDay.getMonth();
        	firstYear = firstDay.getYear();
        }
        if (dbCursor.moveToLast()) {
    		lastDay = new ClassCalendarDay();
        	lastDay.setId(dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_id))));
    		lastDay.setDatum_sort(this.datum_sort = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_sort))));
    		lastDay.setDatum_show(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_show))));
    		lastDay.setWochentag(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_wochentag))));
    		lastDay.setKw(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_kw))));
    		lastDay.setFerientag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_ferientag))));
    		lastDay.setFeiertag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_feiertag))));
        	lastMonth = lastDay.getMonth();
        	lastYear = lastDay.getYear();
            dbCursor.close();
        }
	}

	/* aktuellen Tag ermittelbn */
	private void GetToday() {
		sortFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
	    String todayDate = sortFormat.format(Calendar.getInstance().getTime());
    	today = new ClassCalendarDay();
    	if (this.FindDateByDate(todayDate)) {
    		today.setId(this.id);
    		today.setDatum_sort(this.datum_sort);
    		today.setDatum_show(this.datum_show);
    		today.setWochentag(this.wochentag);
    		today.setKw(this.kw);
    		today.setFerientag(this.getFerientag());
    		today.setFeiertag(this.feiertag);
    	} else {
    		today = firstDay;
    	}
	}
	
	
	/* Kalendertag über _id in der Datenbank abfragen */
	public boolean FindDateByID(long id) {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_kalender), context.getString(R.string.field_id) + " = " + id, null);
        if (dbCursor.moveToFirst()) {
            this.id = dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_id)));
        	this.datum_sort = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_sort)));
            this.datum_show = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_show)));
            this.wochentag = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_wochentag)));
            this.kw = dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_kw)));
            this.ferientag = dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_ferientag)));
            this.feiertag = dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_feiertag)));
            dbCursor.close();
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

	/* Kalendertag über _id in der Datenbank abfragen und als Tag-Objekt zurückliefern*/
	public ClassCalendarDay GetDayByID(long id) {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_kalender), context.getString(R.string.field_id) + " = " + id, null);
        if (dbCursor.moveToFirst()) {
    		ClassCalendarDay actDay = new ClassCalendarDay();
    		actDay.setId(dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_id))));
    		actDay.setDatum_sort(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_sort))));
    		actDay.setDatum_show(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_show))));
    		actDay.setWochentag(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_wochentag))));
    		actDay.setKw(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_kw))));
    		actDay.setFerientag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_ferientag))));
    		actDay.setFeiertag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_feiertag))));
            dbCursor.close();
            return actDay;
        } else {
            dbCursor.close();
    		return null;
        }
	}

	/* Kalendertag über das Sortierdateum in der Datenbank abfragen */
	public boolean FindDateByDate(String date_sort) {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_kalender), context.getString(R.string.field_datum_sort) + " = '" + date_sort + "'", null);
        if (dbCursor.moveToFirst()) {
            this.id = dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_id)));
            this.datum_sort = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_sort)));
            this.datum_show = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_show)));
            this.wochentag = dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_wochentag)));
            this.kw = dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_kw)));
            this.ferientag = dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_ferientag)));
            this.feiertag = dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_feiertag)));
            dbCursor.close();
            return true;
        } else {
            dbCursor.close();
    		return false;
        }
	}

	/* Kalendertage einer bestimmten Woche in einer Hashtable zurückkgeben */
	public List<ClassCalendarDay> GetAllDaysOfWeek(int week_nr) {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_kalender), context.getString(R.string.field_kw) + " = " + week_nr, context.getString(R.string.field_datum_sort) + " ASC");
        if (dbCursor.moveToFirst()) {
        	week = new ArrayList<ClassCalendarDay>();
        	for (Integer i = 1; i<= dbCursor.getCount(); i++) {
        		ClassCalendarDay actDay = new ClassCalendarDay();
        		actDay.setId(dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_id))));
        		actDay.setDatum_sort(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_sort))));
        		actDay.setDatum_show(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_show))));
        		actDay.setWochentag(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_wochentag))));
        		actDay.setKw(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_kw))));
        		actDay.setFerientag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_ferientag))));
        		actDay.setFeiertag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_feiertag))));
        		week.add(actDay);
        		dbCursor.moveToNext();
        	}
	        dbCursor.close();
            return week;
        } else {
            dbCursor.close();
    		return null;
        }
	}

	/* Kalendertage eines bestimmten Monats in einer Hashtable zurückkgeben */
	public List<ClassCalendarDay> GetAllDaysOfMonth(int actmonth, int actyear) {
		db = new DB_DatabaseHelper();
		searchFormat = new SimpleDateFormat("yyyy-MM",Locale.US);
		Date curMonth = new Date(actyear-1900,actmonth-1,1);
		String searchMonth = searchFormat.format(curMonth);
		dbCursor=db.query(context.getString(R.string.table_kalender), context.getString(R.string.field_datum_sort) + " LIKE '" + searchMonth + "%'", context.getString(R.string.field_datum_sort) + " ASC");
        if (dbCursor.moveToFirst()) {
        	month = new ArrayList<ClassCalendarDay>();
        	for (Integer i = 1; i<= dbCursor.getCount(); i++) {
        		ClassCalendarDay actDay = new ClassCalendarDay();
        		actDay.setId(dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_id))));
        		actDay.setDatum_sort(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_sort))));
        		actDay.setDatum_show(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_show))));
        		actDay.setWochentag(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_wochentag))));
        		actDay.setKw(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_kw))));
        		actDay.setFerientag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_ferientag))));
        		actDay.setFeiertag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_feiertag))));
        		month.add(actDay);
        		dbCursor.moveToNext();
        	}
	        dbCursor.close();
            return month;
        } else {
            dbCursor.close();
    		return null;
        }
	}

	/* Ersten Tag des Schuljahres zurückliefern */
	public ClassCalendarDay GetFirstDay() {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_kalender), null, context.getString(R.string.field_datum_sort) + " ASC");
        if (dbCursor.moveToFirst()) {
    		ClassCalendarDay actDay = new ClassCalendarDay();
    		actDay.setId(dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_id))));
    		actDay.setDatum_sort(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_sort))));
    		actDay.setDatum_show(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_show))));
    		actDay.setWochentag(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_wochentag))));
    		actDay.setKw(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_kw))));
    		actDay.setFerientag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_ferientag))));
    		actDay.setFeiertag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_feiertag))));
            dbCursor.close();
            return actDay;
        } else {
            dbCursor.close();
    		return null;
        }
	}

	/* Ersten Tag der übergebenen Woche zurückliefern */
	public ClassCalendarDay GetFirstDayOfWeek(int actWeek) {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_kalender), context.getString(R.string.field_kw) + "= " + Integer.toString(actWeek), context.getString(R.string.field_datum_sort) + " ASC");
        if (dbCursor.moveToFirst()) {
    		ClassCalendarDay actDay = new ClassCalendarDay();
    		actDay.setId(dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_id))));
    		actDay.setDatum_sort(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_sort))));
    		actDay.setDatum_show(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_show))));
    		actDay.setWochentag(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_wochentag))));
    		actDay.setKw(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_kw))));
    		actDay.setFerientag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_ferientag))));
    		actDay.setFeiertag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_feiertag))));
            dbCursor.close();
            return actDay;
        } else {
            dbCursor.close();
    		return null;
        }
	}

	/* Letzten Tag der übergebenen Woche zurückliefern */
	public ClassCalendarDay GetLastDayOfWeek(int actWeek) {
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_kalender), context.getString(R.string.field_kw) + "= " + Integer.toString(actWeek), context.getString(R.string.field_datum_sort) + " ASC");
        if (dbCursor.moveToLast()) {
    		ClassCalendarDay actDay = new ClassCalendarDay();
    		actDay.setId(dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_id))));
    		actDay.setDatum_sort(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_sort))));
    		actDay.setDatum_show(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_datum_show))));
    		actDay.setWochentag(dbCursor.getString(dbCursor.getColumnIndex(context.getString(R.string.field_wochentag))));
    		actDay.setKw(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_kw))));
    		actDay.setFerientag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_ferientag))));
    		actDay.setFeiertag(dbCursor.getInt(dbCursor.getColumnIndex(context.getString(R.string.field_feiertag))));
            dbCursor.close();
            return actDay;
        } else {
            dbCursor.close();
    		return null;
        }
	}
	
	
	/* den auf das übergebene Datum nachfolgenden Tag ermitteln */
	public ClassCalendarDay GetNextDay(String date_sort) {
		if (date_sort == null) {
			return null;
		}
		db = new DB_DatabaseHelper();
		dbCursor=db.query(context.getString(R.string.table_kalender), context.getString(R.string.field_datum_sort) + " = '" + date_sort + "'", null);
        if (dbCursor.moveToFirst()) {
    		if (this.FindDateByID(dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_id))) + 1)) {
            	ClassCalendarDay actDay = new ClassCalendarDay();
        		actDay.setId(this.id);
        		actDay.setDatum_sort(this.datum_sort);
        		actDay.setDatum_show(this.datum_show);
        		actDay.setWochentag(this.wochentag);
        		actDay.setKw(this.kw);
        		actDay.setFerientag(this.getFerientag());
        		actDay.setFeiertag(this.feiertag);
                dbCursor.close();
                return actDay;
    		} else {
        		dbCursor.close();
                return null;
    		}
    		
        } else {
            dbCursor.close();
    		return null;
        }
	}

	/* den auf das übergebene Datum vorhergehenden Tag ermitteln */
	public ClassCalendarDay GetPreviousDay(String date_sort) {
		if (date_sort == null) {
			return null;
		}
		db = new DB_DatabaseHelper();
        dbCursor=db.query(context.getString(R.string.table_kalender), context.getString(R.string.field_datum_sort) + " = '" + date_sort + "'", null);
        if (dbCursor.moveToFirst()) {
    		if (this.FindDateByID(dbCursor.getLong(dbCursor.getColumnIndex(context.getString(R.string.field_id))) - 1)) {
            	ClassCalendarDay actDay = new ClassCalendarDay();
        		actDay.setId(this.id);
        		actDay.setDatum_sort(this.datum_sort);
        		actDay.setDatum_show(this.datum_show);
        		actDay.setWochentag(this.wochentag);
        		actDay.setKw(this.kw);
        		actDay.setFerientag(this.getFerientag());
        		actDay.setFeiertag(this.feiertag);
                dbCursor.close();
                return actDay;
    		} else {
        		dbCursor.close();
                return null;
    		}
    		
        } else {
            dbCursor.close();
    		return null;
        }
	}
}
