package de.androloc.schoolplaner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Enumeration;
import java.util.Hashtable;

public class DB_DatabaseHelper extends SQLiteOpenHelper {

	private static Context appContext;
	private static SQLiteDatabase dbConnection;

	//Konstruktoren
	//-------------
	public DB_DatabaseHelper() {
		//super(appContext,appContext.getResources().getString(R.string.db_name),null,Integer.parseInt(appContext.getResources().getString(R.string.db_version)));
		super(appContext,ClassSettings.getDatabaseName(),null,Integer.parseInt(appContext.getResources().getString(R.string.db_version)));
	}
	public DB_DatabaseHelper(Context context) {
		super(context,ClassSettings.getDatabaseName(),null,Integer.parseInt(context.getResources().getString(R.string.db_version)));
		appContext = context;
		dbConnection = this.getWritableDatabase();
	}

	//Erstellen der Datenbank
	//-----------------------
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Datenbank bei der ersten Initialisierung über sql-File erstellen 
        IO_LineReader readLine = new IO_LineReader("sql/schoolplaner.sql");
        readLine.setContext(appContext);
        if (readLine.OpenFile()) {
	        String sql = null;
        	while ((sql= readLine.ReadLine()) != null) {
        		db.execSQL(sql);
        	}
	        readLine.Close();
        }
	}

	//Datenbankupdates durchführen
	//----------------------------
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		int i;
		//Über alle noch nicht durchgeführten Updates laufen
		for (i=oldVersion+1;i<=newVersion;i++) {
			IO_LineReader readLine = new IO_LineReader("sql/db_upgrade_"+Integer.toString(i)+".sql");
	        readLine.setContext(appContext);
	        if (readLine.OpenFile()) {
		        String sql = null;
	        	while ((sql= readLine.ReadLine()) != null) {
	        		db.execSQL(sql);
	        	}
		        readLine.Close();
	        }
		}
	}

	//Löschen der Datenbank
	public void DeleteDB() {
		this.close();
		appContext.deleteDatabase(ClassSettings.getDatabaseName());
	}

	//Datenbankabfrage durchführenund Cursor zurückgeben
	//--------------------------------------------------
	public Cursor query(String tableName, String queryString,String orderString) {
		//Cursor c = dbConnection.query(tableName, null, queryString, null, null, null, orderString);
		Cursor c = dbConnection.query(tableName, null, queryString, null, null, null, orderString);
		return c;
	}

	//Datensatz hinzufügen
	//--------------------
	public void Add(String tableName, Hashtable<String,String> data) {
		ContentValues values = new ContentValues();
		Enumeration<String> fields = data.keys();
		while (fields.hasMoreElements()) {
			String key = (String) fields.nextElement();
			values.put(key,data.get(key).toString());
		}
		dbConnection.insert(tableName, null, values);
	}

	//Datensatz ändern
	//----------------
	public void Update(String tableName, long id, Hashtable<String,String> data) {
		ContentValues values = new ContentValues();
		Enumeration<String> fields = data.keys();
		while (fields.hasMoreElements()) {
			String key = (String) fields.nextElement();
			values.put(key,data.get(key).toString());
		}
		dbConnection.update(tableName, values, appContext.getString(R.string.field_id)+"=?",new String[] { Long.toString(id) });
	}

	//Datensatz löschen
	//-----------------
	public void Delete(String tableName, long id) {
		dbConnection.delete(tableName, appContext.getString(R.string.field_id)+"=?",new String[] { Long.toString(id) });
	}

	//Kompletten Inhalt einer Tabelle löschen
	//---------------------------------------
	public void ClearTable(String tableName) {
		dbConnection.delete(tableName, null, null);
	}

	//S p e z i a l m e t h o d e n
	//=============================
	
	//ID eines Feiertages abfragen
	//----------------------------
	public long getFeiertagID(String sortDate) {
		String tableName = appContext.getString(R.string.table_feiertage);
		String sql = "SELECT * FROM " + tableName + " WHERE " + appContext.getString(R.string.field_datum_sort)+ " = '" + sortDate + "'";
		Cursor c = dbConnection.rawQuery(sql, null);
		if (c.getCount() > 0) {
			try {
				c.moveToFirst();
				long index = c.getLong(c.getColumnIndex(appContext.getString(R.string.field_id)));
				return index;
			} catch (Exception e) {
				return 0;
			}
		} else {
			return 0;
		}
	}
}
