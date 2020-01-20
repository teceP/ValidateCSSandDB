package logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class Logger {

	/*
	 * Instance of logger
	 */
	private static final Logger instance = new Logger();

	/**
	 * Name for loggerfile
	 */
	public String logname = "Logger";

	/**
	 * Prefix, which will occure in all logs.
	 */
	private static final String PREFIX = "LOG: ";

	/**
	 * Logfolder will be created in this folder.
	 * By default, it will use the same directory, where the .jar file was executed.
	 */
	protected String env = System.getProperty("user.dir");

	/**
	 * Logfile
	 */
	private static File file;

	/**
	 * Logger
	 * @return
	 */
	public static Logger getInstance() {
		instance.createLogFile();
		return instance;
	}

	/**
	 * Creates the logfile.
	 *
	 * Will delete logfolder, if its exists.
	 */
	public void createLogFile(){
		//Determine if a logs directory exists or not.
		File logsFolder = new File(env + '/' + "logs");
		if(!logsFolder.exists()){
			//Create the directory 
			System.err.println("INFO: Creating new logs directory in " + env);
			logsFolder.mkdir();
		}

		//Get the current date and time
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	   	Calendar cal = Calendar.getInstance();
	   	
	   	//Create the name of the file from the path and current time
		logname =  logname + '-' +  dateFormat.format(cal.getTime()) + ".log";
		Logger.file = new File(logsFolder.getName(),logname);
		try{
			if(file.createNewFile()){
				//New file made
				System.err.println("INFO: Creating new log file");	
			}
		}catch(IOException e){
			System.err.println("ERROR: Cannot create log file: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Creates a instance of the logger, if there is no logger instance.
	 *
	 * Otherwhise, will throw an IllegalStateException, in order to prevent reflection.
	 */
	private Logger(){
		if (instance != null){
			//Prevent Reflection
			throw new IllegalStateException("Cannot instantiate a new singleton instance of log");
		}
		this.createLogFile();
	}

	/**
	 * Writes a new line into the logfile.
	 *
	 * @param message
	 */
	public static void log(String message){
		try{
			FileWriter out = new FileWriter(Logger.file, true);
			System.out.println(Logger.PREFIX + message);
			out.write(Logger.PREFIX + message + System.lineSeparator());
			out.close();
		}catch(IOException e){
			System.err.println("ERROR: Could not write to log file");
		}
	}
}
