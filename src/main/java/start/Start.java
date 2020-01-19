package start;

import analyzer.CSSAnalyzer;
import analyzer.FileAnalyzer;
import logger.Logger;
import psql.Connector;

import java.io.File;

public class Start {

    public static void main(String[] args) {

        if (args.length != 2) {
            Logger.log("Wrong parameter length.");
            Logger.log("Try something like: java -jar CSSCreator.jar database.txt 80");
            System.exit(1);
        }

        File database = new File(args[0]);

        Logger.log("Percentage will be set to " + args[1] + ".");

        if (!database.exists()) {
            Logger.log("Databaseconfig '" + database.getName() + "' doesnt exists.");
            System.exit(1);
        }

        if (args.length == 2) {
            if (Float.parseFloat(args[1]) > 100 || Float.parseFloat(args[1]) < 50) {
                Logger.log("Your percentage parameter was more than 100, or less then 50.");
                Logger.log("By default, this program stops, if your percentage parameter was less than 50 because it wont get good results.");
                System.exit(1);
            } else {
                Logger.log("Matches will be checked be with min. '" + args[1] + "%'.");
                Logger.log("It is recommended, to use high percentage.");
                Logger.log("Could cause bad results, if your using low percentage!");
                Logger.log("Using Jaro Winkler Similarity for this option.");
            }
        }

        Logger.log("Starting CSSCreatorForOHDM..........");

        //Phase 1
        Connector connector = new Connector(args[0]);
        connector.start();

        CSSAnalyzer cssAnalyzer = new CSSAnalyzer();
        Thread cssAT = new Thread(cssAnalyzer);

        cssAT.start();

        try {
            cssAT.join();

            Double percentageArgument = Double.parseDouble(args[1]);

            FileAnalyzer fileAnalyzer = new FileAnalyzer(percentageArgument);
            fileAnalyzer.searchForMatches();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /**
         * TODO
         *
         * Add cleaner here
         *
         */

    }
}
