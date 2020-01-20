package psql;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import logger.Logger;


public class Connector{

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
	 * Arguments with all of the database-connection informations.
	 */
	private String argument;

	/**
	 * Parameters has to be seperated by a ";"
	 */
	private final String DELIMETER = ";";

	/**
	 * Connects to a postgre database
	 *
	 * @param argument Database configs like password, username, etc. Should be provided, when the .jar is executed by the user.
	 */
	public Connector(String argument) {
				
		/*
		 * By convention: 
		 * 
		 * arguments[0] = Database Infos (textfile)
		 * arguments[1] = Css Infos (folder)
		 * 
		 */

		this.argument = argument;
	}

	public void start(){
		this.readDatabaseConfig(argument);
	}
	
	/**
	 * Reads the Databaseconfig file
	 * @param configFile
	 */
	private void readDatabaseConfig(String configFile) {
		
		//relative Path - needs to be in same folder
		File file = new File(configFile);
				
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String config = "";
			String temp = "";
			
			while((temp = br.readLine()) != null) {
				config += temp;
			}

			br.close();
			this.getDatabaseInfos(config);
		} catch (FileNotFoundException e) {
			System.out.println("File not found. Has to be in same directory as the .jar file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Scans the databaseconfig and put the values to the attributes
	 * @param config
	 */
	private void getDatabaseInfos(String config) {
		Logger.log("*****************************************");
		Logger.log("Start scanning databaseinformations...");
		Logger.log("*****************************************");
		Logger.log(config);
		
		String[] tokens = config.split(DELIMETER);

		/*
		 * By Convention: serverid;port;username;password;database_name;schema
		 * 
		 * Example: localhost;5432;postgres;7777777;osm;rendering;
		 * 
		 */
		try {
			server = tokens[0];
			port = Integer.parseInt(tokens[1]);
			username = tokens[2];
			password = tokens[3];
			dbname = tokens[4];
			schema = tokens[5];
		} catch (Exception e) {
			System.out.println("Check you databaseconfigfile.\nNeed Informations like: serverid;port;username;password;database_name;schema");
		}
		
		Logger.log("*****************************************");
		Logger.log("Databaseinformations are scanned: ");
		Logger.log("*****************************************");
		Logger.log("Server: " + this.server);
		Logger.log("Port: "+ this.port);
		Logger.log("Username: " + this.username);
		Logger.log("Password: " + this.password);
		Logger.log("DBName: " + this.dbname);
		Logger.log("Schema: " + this.schema);
		
		this.connect();
	}
	
	private void connect() {
		PostgreConnection connection = new PostgreConnection(server, port, username, password, dbname, schema);
	}
}
