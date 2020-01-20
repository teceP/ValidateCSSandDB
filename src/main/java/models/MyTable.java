package models;

import java.util.ArrayList;
import java.util.List;

public class MyTable {
	
	
	/*
	 * TODO
	 * Currently, only the tablename gets safed.
	 * For further use, maybe also the columns should be safed.
	 * 
	 */

	/**
	 * Name of the databasetable
	 */
	private String tableName;

	/**
	 * Names of the columns in the DB-table
	 */
	private List<String> columns;

	/**
	 * A databasetable with, their columnnames
	 * @param name
	 */
	public MyTable(String name) {
		this.tableName = name;
		columns = new ArrayList<String>();
	}
	
	public void setTablename(String name) {
		this.tableName = name;
	}

	public List<String> getColumn() {
		return columns;
	}

	public void setColumn(List<String> column) {
		this.columns = column;
	}
	
	public void addColumn(String column) {
		this.columns.add(column);
	}
	
	/**
	 * 
	 * @param column
	 * @return false if column doesnt exists
	 * @return true if column was deleted
	 */
	public boolean deleteColumn(String column) {
		if(this.columns.contains(column)) {
			this.columns.remove(column);
			return true;
		}
		return false;
	}
	
	public String getTableName() {
		return this.tableName;
	}

}
