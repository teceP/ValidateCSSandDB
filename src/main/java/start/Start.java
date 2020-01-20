package start;

import analyzer.CSSAnalyzer;
import analyzer.FileAnalyzer;
import logger.Logger;
import psql.Connector;

import java.io.File;

public class Start {

    public static void main(String[] args) {

        /**
         * Check param.length
         */
        if (args.length != 2) {
            Logger.log("Wrong parameter length.");
            Logger.log("Try something like: java -jar CSSCreator.jar database.txt 80");
            System.exit(1);
        }

        /**
         * Databaseconfig.file
         */
        File database = new File(args[0]);

        Logger.log("Percentage will be set to " + args[1] + ".");

        /**
         * If databaseconfigfile doesnt exists, shutdown.
         */
        if (!database.exists()) {
            Logger.log("Databaseconfig '" + database.getName() + "' doesnt exists.");
            System.exit(1);
        }

        /**
         * Check if the percentage parameter is a number.
         * If not: shutdown the program.
         */
        Double checkForNaN = Double.parseDouble(args[1]);

        if(checkForNaN == Double.NaN){
            Logger.log("Your second parameter for the percentage-rate is not a number.");
            System.exit(1);
        }


        /**
         * Information for the user, since this program uses the JarowWinkler Algorithm,
         * it should used a high percentage for comparing words.
         *
         * (See more in the FileAnalyzer Class, method: checkForMatch(..))
         */
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

        /**
         * Phase 1: Connect to the database and analyze database tables & columns.
         *          Store informations as artifacts.
         *
         */
        Connector connector = new Connector(args[0]);
        connector.start();

        /**
         * Phase 2: Download the latest repository of css styles.
         *          Store all cssfilenames as artifacts.
         *
         */
        CSSAnalyzer cssAnalyzer = new CSSAnalyzer();
        Thread cssAT = new Thread(cssAnalyzer);

        cssAT.start();

        try {
            cssAT.join();

            /**
             * Phase 3: Analyze CSSFiles for classnames.
             *          Compare classnames with database-tablenames and get matches, if
             *          the accurency is more then the given percentage parameter, which the
             *          user sets when he starts this program. (Second parameter)
             *          Store matches in the artifact folder.
             *
             */
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
