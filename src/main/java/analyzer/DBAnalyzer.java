package analyzer;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import logger.Logger;
import models.MyTable;
import psql.PostgreConnection;
import storage.StorageAdmin;
import storage.StorageAdminInterface;

public class DBAnalyzer {

	/**
	 * PostgreConnection
	 */
	private PostgreConnection connection;

	/**
	 * List of database tables
	 */
	private List<MyTable> tables;

	/**
	 * DatasetMeta of database
	 */
	private DatabaseMetaData md;

	/**
	 * ResultSet for the tables of the given database
	 */
	private ResultSet rsTables;

	/**
	 * ResultSet for the columns of a specific table, in a given database
	 */
	private ResultSet rsColumns;

	/**
	 * Analyzes the database for tables and column of these tables.
	 * Stores informations in the artifact folder.
	 *
	 * @param connection
	 */
	public DBAnalyzer(PostgreConnection connection) {
		this.connection = connection;
		this.tables = new ArrayList<MyTable>();
	}

	public void start() {
		this.getTables();
	}

	/**
	 * Collects all tables from the database under a specific schema.
	 * Schema was set in the databaseconfig file, which will be anaylized after the
	 * program starts.
	 *
	 * Databaseconfigfile = first parameter/argument
	 *
	 * @return
	 */
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
				Logger.log("Added " + this.tables.size() + " tables.");
				}

			this.storeData();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * Converts the tableslist, to a list of strings, just with the name of the tables.
	 * Calls the StorageAdmin, to store this data.
	 *
	 * TODO
	 * Collect also the column names.
	 * Use the StorageAdminHelper and implement in this class a new helpermethod.
	 *
	 */
	public void storeData(){
		StorageAdminInterface sa = new StorageAdmin();
		List<String> tableNames = new ArrayList<>();

		for(MyTable t : this.tables){
			tableNames.add(t.getTableName());
		}

		sa.storeList(tableNames, StorageAdminInterface.TABLE_NAMES, true);
	}

}
