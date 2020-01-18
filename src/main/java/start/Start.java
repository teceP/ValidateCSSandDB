package start;

import analyzer.CSSAnalyzer;
import analyzer.FileAnalyzer;
import logger.Logger;
import psql.Connector;

public class Start {
		
	public static void main(String[] args) {	
		
		
		if(args.length == 2) {
			if(Float.parseFloat(args[1]) > 100 || Float.parseFloat(args[1]) < 50) {
				Logger.log("Your percentage parameter was more than 100, or less then 50.");
				Logger.log("By default, this program stops, if your percentage parameter was less than 50 because it wont get good results.");
				System.exit(1);
			}
		}
		
		if(args.length == 1) {
			
			Logger.log("Starting CSSCreatorForOHDM..........");
			
			//Phase 1
			Connector connector = new Connector(args[0]);
			connector.start();
			
			CSSAnalyzer cssAnalyzer = new CSSAnalyzer();
			Thread cssAT = new Thread(cssAnalyzer);

			cssAT.start();
			
			try {
				cssAT.join();
				
				if(args.length == 2) {
					FileAnalyzer fileAnalyzer = new FileAnalyzer(Double.parseDouble(args[1]));
					fileAnalyzer.analyze();					
				}else {
					FileAnalyzer fileAnalyzer = new FileAnalyzer();
					fileAnalyzer.analyze();	
				}
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
			
		}else if(args.length == 2) {
				
			Logger.log("You choosed the percentage accordance option.");
			Logger.log("It is recommended, to use high percentage.");
			Logger.log("Could cause bad results, if your using low percentage!");
			Logger.log("Using Jaro Winkler Similarity for this option.");
		}
		else {
			Logger.log("Parameters not correct.");
			Logger.log("Try something like: java -jar CSSCreator.jar database.txt");
		}

		/**
		 * TODO
		 * 
		 * Add cleaner here 
		 * 
		 */
		
	}
	
}
