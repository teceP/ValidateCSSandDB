package analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import enumerations.CSSIdentifier;
import logger.Logger;
import models.Accordance;
import models.Found;
import models.MyTable;

public class FileAnalyzer {
	
	private File[] files;
	private Double percentage;
	private List<MyTable> tables;
	
	public FileAnalyzer(File[] files, List<MyTable> tables) {
		this.files = files;
		this.tables = tables;
	}
	
	public FileAnalyzer(File[] files, List<MyTable> tables, double percentage) {
		this.files = files;
		this.tables = tables;
		this.percentage = percentage;
	}
	
	/**
	 * Searches for a given CSS identifier in a given CSS file.
	 * 
	 * @param CSS file
	 * @return A list of Found-Objects, which contains the css-file name, the first character index, the length of the classname, which was found in a given CSS file.
	 */
	public List<Found> scanFile(File file, CSSIdentifier identifier) {
		Logger.log("Scanning file '" + file.getName() + "' for "+ identifier.toString() + " - CSS identifier.");
		List<Found> found = new ArrayList<Found>();
		
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			String className = "";
			int indexRow = 0;// - Zeile
			int firstIndexColumn = 0; // | Spalte
			int lastIndexColumn = 0;
			char temp = 'x';
			
			while((line = br.readLine()) != null) {
				
				if(line.contains(identifier.getString())) {
								
					//Needs / because of apostrophe
					while(temp != '\'') {
						firstIndexColumn++;
						temp = line.charAt(firstIndexColumn);
					}
					
					firstIndexColumn++;
					lastIndexColumn = firstIndexColumn + 1;
					temp = 'x';
					
					while(temp != '\'') {
						lastIndexColumn++;
						temp = line.charAt(lastIndexColumn);
					}
					
					className = line.substring(firstIndexColumn, lastIndexColumn);
										
					Found f = new Found(file, className, indexRow, firstIndexColumn, lastIndexColumn);
					found.add(f);
					
					Logger.log("Found: " + f.getName() + " at row " + indexRow + " at column from " + firstIndexColumn + " to " + lastIndexColumn + ".");
					
					//Set variables back
					firstIndexColumn = 0;
					lastIndexColumn = 0;
					className = "";
					temp = 'x';
				}
				indexRow++;
			}
				
			if(found.size() != 0) {
				Logger.log("Found " + found.size() + " classes.");
			}
		} catch (FileNotFoundException e) {
			//Should never happen, expect user deleted files manually.
			Logger.log("FAIL: File not exist. Should definatally exist." + System.lineSeparator() + "Make sure no process deletes any files from 'osm-styles' folder.");
			e.printStackTrace();
		} catch (IOException e) {
			Logger.log("IOException - while trying to search a file for a given key. - Check BufferedReader operations.");
			e.printStackTrace();
		}
		
		return found;
	}
	
	
	
	/**
	 * Checks if there are any accordances between the given css class
	 * and a database table name.
	 * 
	 * Use this method, if program is runned without '-p' parameter
	 * @param cssClass
	 * @return A list of accordances.
	 * @return Null if there are no accordances.
	 */
	public List<Accordance> checkAccordance(File file, String cssClass, List<MyTable> tableNames) {
		List<Accordance> accordances = new ArrayList<>();
		
		Logger.log("Scan file '" + file.getName() + "' for accordances.");
		Logger.log("Accordances will be displayed like:" + System.lineSeparator() + "identifier_from_css_file - tablename");

		for(MyTable current : tableNames) {
			if(cssClass.equals(current)) {
				accordances.add(new Accordance(file, cssClass, current.getTableName()));
				Logger.log("Found new accordance: " + cssClass + " - " + current.getTableName());
			}
		}
		return accordances;
	}
	
	/**
	 * Checks if there are any accordances between the given css class
	 * and a database table name.
	 * 
	 * Use this method, if program is runned with a second parameter behind the database configs liks:
	 * java -jar thistool.jar database.txt 87.231
	 * 
	 * Uses Apache Commons Lang 3 library.
	 * @param cssClass
	 * @return A list of accordances.
	 * @return Null if there are no accordances.
	 */
	public List<Accordance> checkAccordanceWithPercentage(File file, String cssClass, List<MyTable> tableNames, double minPercentage) {
		List<Accordance> accordances = new ArrayList<>();
		JaroWinklerSimilarity jws = new JaroWinklerSimilarity();
		//-1 to = never been set by JaroWinkerMethod
		double actualPercentage = -1;	
		double allPercentages = 0;
		
		Logger.log("Scan file '" + file.getName() + "' for accordances with min. percantage: " + minPercentage + ".");
		Logger.log("Accordances will be displayed like:" + System.lineSeparator() + "identifier_from_css_file - tablename with xx %");

		for(MyTable current : tableNames) {
			actualPercentage = jws.apply(cssClass, current.getTableName());
			
			if(actualPercentage >= minPercentage) {
				accordances.add(new Accordance(file, cssClass, current.getTableName(), actualPercentage));	
				allPercentages += actualPercentage;
				Logger.log("Found new accordance: " + cssClass + " - " + current.getTableName() + " with " + actualPercentage + " %.");
			}
		}
		
		Logger.log("Found " + accordances.size() + " accordances in this file." 
					+ System.lineSeparator() + "Average percentage: " + allPercentages/accordances.size() + ".");
		return accordances;
	}

	public void analyze() {
		
		Logger.log("Start analyzing files ...");
		
		List<List<Found>> found = new ArrayList<>();
		
		for(File f : files) {
			found.add(this.scanFile(f, CSSIdentifier.CLASSNAME));
		}
		
		if(this.percentage == null) {
			
			for(List<Found> f : found) {
				for(Found ff : f) {
					this.checkAccordance(ff.getFile(), ff.getName(), this.tables);					
				}
			}
		}else {
			for(List<Found> f : found) {
				for(Found ff : f) {
					this.checkAccordanceWithPercentage(ff.getFile(), ff.getName(), this.tables, this.percentage);					
				}
			}
		}
		
	}

}
