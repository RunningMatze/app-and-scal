package de.androloc.schoolplaner;

public class StructFeiertag {

	protected String realDate;
	protected String sortDate;
	protected String caption;
	
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
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	
	public void SplitParts(String feiertagLine) {
		
		String[] temp1;
		String[] temp2;
		
		temp1=feiertagLine.split("@");
		try {
			this.caption = temp1[1];
		} catch (Exception e) {
			this.caption = "";
		}
		
		try {
			temp2 = temp1[0].split("\\|");
			this.realDate=temp2[0];
			this.sortDate=temp2[1];
		} catch (Exception e) {
			this.realDate="";
			this.sortDate="";
		}
		
	}
}
