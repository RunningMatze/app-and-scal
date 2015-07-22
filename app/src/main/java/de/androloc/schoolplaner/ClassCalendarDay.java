package de.androloc.schoolplaner;

public class ClassCalendarDay {

	/* Member-Variablen */
	private long id;
	private String datum_sort;
	private String datum_show;
	private String wochentag;
	private int kw;
	private int feiertag;
	private int ferientag;

	/*Getters und Setters*/
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
	
	public ClassCalendarDay() {
		super();
		id = 0;
		datum_sort = "";
		datum_show = "";
		wochentag = "";
		kw = 0;
		feiertag = 0;
		ferientag = 0;
	}

	/* Monat zurückliefern */
	public int getMonth() {
		int month = 0;
		month = Integer.parseInt(datum_sort.substring(5, 7));
		return month;
	}
	/* Monat zurückliefern */
	public int getYear() {
		int year = 0;
		year = Integer.parseInt(datum_sort.substring(0, 4));
		return year;
	}
}
