package models;

import java.io.File;

public class Accordance {
	
	private String identifier;
	private String tableName;
	private double percentage;
	private File cssFile;
	
	public Accordance(File cssFile, String identifier, String tableName) {
		this.identifier = identifier;
		this.cssFile = cssFile;
		this.tableName = tableName;
	}
	
	public Accordance(File cssFile, String identifier, String tableName, double percentage) {
		this.identifier = identifier;
		this.cssFile = cssFile;
		this.tableName = tableName;
		this.percentage = percentage;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getTableName() {
		return tableName;
	}

	public double getPercentage() {
		return percentage;
	}

	public File getCssFile() {
		return cssFile;
	}
	
	
	
	

}
