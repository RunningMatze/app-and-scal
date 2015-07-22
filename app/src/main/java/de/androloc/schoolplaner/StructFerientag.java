package de.androloc.schoolplaner;

public class StructFerientag {

	protected String realDate;
	protected String sortDate;
	
	public String getRealDate() {
		return realDate;
	}
	public void setRealDate(String realDate) {
		this.realDate = realDate;
	}
	public String getSortDate() {
		return sortDate;
	}
	public void setSortDate(String sortDate) {
		this.sortDate = sortDate;
	}
	
	public void SplitParts(String ferienLine) {
		
		String[] temp1;
		
		try {
			temp1 = ferienLine.split("\\|");
			this.realDate = temp1[0];
			this.sortDate = temp1[1];
		} catch (Exception e) {
			this.realDate="";
			this.sortDate="";
		}
		
	}

}
