package de.androloc.schoolplaner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class CalendarIniReader {

	private final static String TAG = IO_LineReader.class.getSimpleName();
	
	private InputStreamReader streamReader = null;
	private BufferedReader buffReader = null;
	private AssetManager assManager;
	
	//Context der Activity
	//====================
	private Context context;
	public void setContext(Context context) {
		this.context = context;
	}

	//Property-Filename
	//=================
	private String fileName = null;
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	//Property-Ländercode
	//===================
	private String landKey = null;
	public void setLandKey(String landKey) {
		this.landKey = landKey;
	}

	//Erster Schultag
	//===============
	private String ersterTag = null;
	public String getErsterTag() {
		return ersterTag;
	}

	//Letzter Schultag
	//================
	private String letzterTag = null;
	public String getLetzterTag() {
		return letzterTag;
	}

	//Liste mit Feiertagen
	//====================
	private List<String> listFeiertage = new ArrayList<String>();
	public List<String> getListFeiertage() {
		return listFeiertage;
	}

	//Liste mit Ferientagen
	//=====================
	private List<String> listFerientage = new ArrayList<String>();
	public List<String> getListFerientage() {
		return listFerientage;
	}

	
	//Konstruktor mit Dateiname und Ländercode
	//========================================
	public CalendarIniReader(String fileName, String landKey){
		this.fileName = fileName;
		this.landKey = landKey;
	}


	//Alle Kalendereinstellungen auslesen
	public Boolean ReadCalendarData() {
			
		if (this.OpenFile() == false) {
			return false;
		}
		
		String curLine = null;

		//1. [AL]-Schlüssel suchen 
		//----------------------------------------------------------------------------		
		curLine=ReadLine();
		while (!curLine.equals("[AL]") && (curLine != null)) {
			curLine=ReadLine();
		}
		if (curLine == null) return false;
		//----------------------------------------------------------------------------		

		//1.1 [Feiertage]-Schlüssel suchen 
		//----------------------------------------------------------------------------		
		curLine=ReadLine();
		while (!curLine.equals("[Feiertage]") && (curLine != null)) {
			curLine=ReadLine();
		}
		if (curLine == null) return false;
		//----------------------------------------------------------------------------		

		//1.2 Alle Einträge bis zum [ENDE]-Tag lesen und an Liste übergeben 
		//-----------------------------------------------------------------		
		curLine=ReadLine();
		while (!curLine.equals("[ENDE]") && (curLine != null)) {
			listFeiertage.add(curLine);
			curLine=ReadLine();
		}
		if (curLine == null) return false;
		//----------------------------------------------------------------------------		
		
		//2. Länder-Schlüssel suchen 
		//----------------------------------------------------------------------------		
		curLine=ReadLine();
		while (!curLine.equals("[" + landKey + "]") && (curLine != null)) {
			curLine=ReadLine();
		}
		if (curLine == null) return false;
		//----------------------------------------------------------------------------		

		//2.1 Erster und letzter Schultag 
		//-------------------------------		
		ersterTag = ReadLine();
		if (ersterTag == null) return false;
		letzterTag = ReadLine();
		if (letzterTag == null) return false;
		
		//2.2 [Feiertage]-Schlüssel suchen 
		//--------------------------------------------------		
		curLine=ReadLine();
		while (!curLine.equals("[Feiertage]") && (curLine != null)) {
			curLine=ReadLine();
		}
		if (curLine == null) return false;
		//----------------------------------------------------------------------------		
		
		//2.3 Feiertage bis zum [Ferien]-Schlüssel auslesen und an Liste übergeben 
		//----------------------------------------------------------------------------		
		curLine=ReadLine();
		while (!curLine.equals("[Ferien]") && (curLine != null)) {
			listFeiertage.add(curLine);
			curLine=ReadLine();
		}
		if (curLine == null) return false;
		//----------------------------------------------------------------------------		
		
		//2.4 Ferien bis zum [ENDE]-Tag einlesen 
		//----------------------------------------------------------------------------		
		curLine=ReadLine();
		while (!curLine.equals("[ENDE]") && (curLine != null)) {
			listFerientage.add(curLine);
			curLine=ReadLine();
		}
		if (curLine == null) return false;
		//----------------------------------------------------------------------------		
		
		//Datei schließen
		this.Close();
		
		return true;
	}
	
	//Nächste Zeile auslesen
	//======================
	private String ReadLine() {
		
		String nextLine = null;
		try {
			if ((nextLine = buffReader.readLine()) != null) {
				nextLine = nextLine.toString();
			} else {
				nextLine = null;
			}
		} catch (Throwable t) {
			Log.e(TAG,context.getString(R.string.error_file_read,this.fileName));
		}
		
		return nextLine;
	}

	
	//Datei öffnen
	//============
	private Boolean OpenFile() {
		if (this.fileName == null || this.context == null) {
			return false;
		} else {		
		
			try {
				assManager = context.getResources().getAssets();
				streamReader = new InputStreamReader(assManager.open(fileName, AssetManager.ACCESS_BUFFER));
				buffReader = new BufferedReader(streamReader);
				return true;
			} catch (Throwable t) {
				Log.e(TAG,context.getString(R.string.error_file_open,this.fileName));
				return false;
			}
		}
	}

	//Dateiobjekte schließen
	//======================
	private void Close() {
		if (buffReader != null) {
			try {
				buffReader.close();
			} catch (Exception e) {
			}
		}
		if (streamReader != null) {
			try {
				
				streamReader.close();
			} catch (Exception e) {
			}
		}
	}

}
