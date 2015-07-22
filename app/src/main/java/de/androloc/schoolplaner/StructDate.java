package de.androloc.schoolplaner;

public class StructDate {

	protected int tag;
	protected int monat;
	protected int jahr;
	
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public int getMonat() {
		return monat;
	}
	public void setMonat(int monat) {
		this.monat = monat;
	}
	public int getJahr() {
		return jahr;
	}
	public void setJahr(int jahr) {
		this.jahr = jahr;
	}
	
	public StructDate SplitDate(String datum,String delim) {
		
		String[] temp;
		if (delim.equals(".")) delim = "\\.";
		temp = datum.split(delim);
		if (delim.equals("\\.")) {
			this.tag = Integer.parseInt(temp[0]);
			this.monat = Integer.parseInt(temp[1])-1;
			this.jahr = Integer.parseInt(temp[2])-1900;
		} else {
			this.tag = Integer.parseInt(temp[2])-1900;
			this.monat = Integer.parseInt(temp[1])-1;
			this.jahr = Integer.parseInt(temp[0]);
		}
		return this;
	}
}
