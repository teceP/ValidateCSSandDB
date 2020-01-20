package psql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import analyzer.DBAnalyzer;
import logger.Logger;

public class PostgreConnection {

	/**
	 * IP Adresse
	 */
	private String server;

	/**
	 * Connectionport on server
	 */
	private int port;

	/**
	 * Username for database
	 */
	private String username;

	/**
	 * Password for database
	 */
	private String password;

	/**
	 * Databasename
	 */
	private String dbname;

	/**
	 * Database schema
	 */
	private String schema;

	/**
	 * Url to the database and with the psql-prefix.
	 */
	private String url;

	/**
	 * Properties of connection
	 */
	private Properties props;

	/**
	 * Databaseconnection
	 */
	private Connection connection;

	/**
	 * Connects to a Postgre Database and searches for Metadata
	 *
	 * @param server
	 * @param port
	 * @param username
	 * @param password
	 * @param db
	 * @param schema
	 */
	public PostgreConnection(String server, int port, String username, String password, String db, String schema) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.password = password;
		this.dbname = db;
		this.schema = schema;
		
		if(this.connect()) {
			DBAnalyzer analyzer = new DBAnalyzer(this);
			analyzer.start();
		}else {
			Logger.log("Program stopped.");
			System.exit(1);
		}
	}

	/**
	 * Connects to a database, based on the given databaseinformations
	 * @return true if connects without any exceptions
	 */
	private boolean connect() {
		
		Logger.log("Try to connect to server...");
		url = "jdbc:postgresql://" + server + ":" + port + "/" + dbname;
		
		props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		props.setProperty("ssl", "true");
		
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		Logger.log("Connection to '"+ url +"' is established.");
		Logger.log("Wehre '" + dbname + "' is your database.");
		return true;
	}

	/**
	 * Disconnects
	 * @return true if no exception occured
	 */
	public boolean disconnect() {
		
		try {
			this.connection.close();
			Logger.log("Connection to '"+ url +"' is closed.");
		} catch (SQLException e) {
			Logger.log("Couldnt close connection to '"+ url +"'.");
			e.printStackTrace();
		}
		
		return true;
	}

	/**
	 * Loads DatabaseMetaData from the database
	 * @return databaseMetaData
	 */
	public DatabaseMetaData getMetaData() {
		
		try {
			return this.connection.getMetaData();
		} catch (SQLException e) {
			Logger.log("FAIL: Couldnt get MetaData.");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the databaseschemaname
	 * @return
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * Returns the databasename
	 * @return
	 */
	public String getDbName() {
		return this.dbname;
	}
	

}
