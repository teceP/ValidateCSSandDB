package analyzer;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logger.Logger;
import memento.Safe;
import models.MyTable;
import psql.PostgreConnection;

public class DBAnalyzer {
	
	private PostgreConnection connection;
	private List<MyTable> tables;
	
	private DatabaseMetaData md;
	private ResultSet rsTables;
	private ResultSet rsColumns;
	
	private int counter;
	
	public DBAnalyzer(PostgreConnection connection) {
		this.connection = connection;
		this.tables = new ArrayList<MyTable>();
		counter = 1;
	}

	public void start() {
		this.getTables();
	}
	
	private boolean getTables() {
		Logger.log("**************************************");
		Logger.log("Getting tablenames and colomnnames...");
		
		md = connection.getMetaData();
		
		try {
			rsTables = md.getTables(connection.getDbName(), connection.getSchema(), null, new String[]{"TABLE"});
			
			MyTable tempTable;
			
			while(rsTables.next()) {
				tempTable = new MyTable("");
				Logger.log(rsTables.getString("TABLE_NAME"));
				tempTable.setTablename(rsTables.getString("TABLE_NAME"));
				
				rsColumns = md.getColumns(null, connection.getSchema(),rsTables.getString("TABLE_NAME") , null);
				
				while(rsColumns.next()) {
					tempTable.addColumn(rsColumns.getString("COLUMN_NAME"));
					Logger.log("---" +  rsColumns.getString("COLUMN_NAME"));
				}
				
				this.tables.add(tempTable);
				Logger.log("Added " + counter + " tables.");
				counter++;
				}
			
			Safe.safe(this.tables);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public List<MyTable> tables(){
		return this.tables;
	}
}
