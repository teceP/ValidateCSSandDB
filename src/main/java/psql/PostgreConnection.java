package psql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import analyzer.DBAnalyzer;
import logger.Logger;

public class PostgreConnection {
		
	private String server;
	private int port;
	private String username;
	private String password;
	private String dbname;
	private String schema;
	
	private String url;
	private Properties props;
	private Connection connection;
	
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
	
	public DatabaseMetaData getMetaData() {
		
		try {
			return this.connection.getMetaData();
		} catch (SQLException e) {
			Logger.log("FAIL: Couldnt get MetaData.");
			e.printStackTrace();
			return null;
		}

	}
	
	
	public String getSchema() {
		return this.schema;
	}


	public String getDbName() {
		return this.dbname;
	}
	

}
