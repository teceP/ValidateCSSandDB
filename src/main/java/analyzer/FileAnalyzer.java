package analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import enumerations.CSSIdentifier;
import logger.Logger;
import models.Match;
import models.CssClass;
import models.MyTable;
import storage.StorageAdmin;
import storage.StorageAdminInterface;

public class FileAnalyzer {

    /**
     * CSS files
     */
    private File[] cssFiles;

    /**
     * How much a accordance has to be, if method is using
     * JarowWinkler method.
     * <p>
     * Used in checkAccordanceWithPercentage(..., percentage)-method.
     */
    private Double percentage;

    /**
     * All percentages of the matches are added in here.
     * Use this for the logger, to store die percentage accuracy of this run.
     *
     * completePercentage/matches.size()
     */
    private double completePercentage;

    /**
     * Decimalformat, for output and storages of accuracy-percentage values.
     */
    private DecimalFormat df;

    /**
     * Renderingdatabasetables of the OHDM database/your database.
     */
    private List<MyTable> tables;

    /**
     * A list of CssClass objects, which contains informations about the
     * classname, length, position in css file
     */
    private List<CssClass> cssClasses;

    /*
     * A list of Strings, which represent css-file-names, which has no classes inside.
     *
     * Info: A css file dont needs seperate classes.
     * If it has no class in it, the filename can be used as a classname.
     */
    private List<String> cssFilesWithoutClasses;

    /**
     * All matches, which where found in the checkMatch Method will be stored in this list.
     * A match always contains a CSSFile/CSSClass and a database tablename.
     *
     * It contains also a String called suggestion, which represents the suggest, what
     * the CssFile / CssClass should be named, in order to be useable for the rendering
     * in the geoserver. The name should match the database table/or column name.
     */
    private List<Match> allMatches;


    /**
     * Can analyze CSSFiles for there classes and compare this classes,
     * to database tablenames (further implementation: database columnnames).
     *
     * Founded matches will be stored in the artifact folder. Filename: matches
     *
     */
    public FileAnalyzer(double percentage) {
        this.df = new DecimalFormat("#.####");
        this.completePercentage = 0;
        this.cssClasses = new ArrayList<>();
        this.cssFilesWithoutClasses = new ArrayList<>();

        //Load ohdm tables, which where anaylized earlier
        this.loadTables();

        //Load cssfile(names), which where analyzed earlier
        this.loadFiles();

        //set percentage parameter for later called accurance methode
        this.percentage = percentage;
    }

    /**
     * Loads osm-style CSS-Filenames from the downloaded github-Repository of osm-styles by geosolutions.
     * For more informations, look in the GitProvider class.
     */
    public void loadFiles() {
        StorageAdminInterface sa = new StorageAdmin();

        List<String> filesRestored = sa.restoreList(StorageAdminInterface.CSS_FILES, true);
        this.cssFiles = new File[filesRestored.size()];
        File temp;

        for (int i = 0; i < filesRestored.size(); i++) {
            temp = new File(StorageAdminInterface.OSM_STYLES_FOLDER_PREFIX + filesRestored.get(i));
            cssFiles[i] = temp;
            Logger.log("Reloaded file from storage: " + temp.getName());
        }
    }

    /**
     * Loads tables from artifact folder.
     */
    public void loadTables() {
        StorageAdminInterface sa = new StorageAdmin();
        tables = sa.restoreTables();
    }

    /**
     * Searches for a given CSS identifier/rule, in a given CSS file.
     *
     * @param file file
     * @return A list of Found-Objects, which contains the css-file name,
     * the first character index, the length of the classname, which was found in a given CSS file.
     */
    public void scanFile(File file, CSSIdentifier identifier) {
        Logger.log("Scanning file '" + file.getName() + "' for " + identifier.toString() + " - CSS identifier.");

        List<CssClass> tempCssClass = new ArrayList<>();

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String className = "";
            int indexRow = 0;// - Zeile
            int firstIndexColumn = 0; // | Spalte
            int lastIndexColumn = 0;
            char temp = 'x';

            while ((line = br.readLine()) != null) {

                if (line.contains(identifier.getString())) {

                    //Needs \ because of apostrophe
                    while (temp != '\'') {
                        firstIndexColumn++;
                        temp = line.charAt(firstIndexColumn);
                    }

                    firstIndexColumn++;
                    lastIndexColumn = firstIndexColumn + 1;
                    temp = 'x';

                    while (temp != '\'') {
                        lastIndexColumn++;
                        temp = line.charAt(lastIndexColumn);
                    }

                    className = line.substring(firstIndexColumn, lastIndexColumn);

                    CssClass f = new CssClass(file, className, indexRow, firstIndexColumn, lastIndexColumn);

                    tempCssClass.add(f);

                    Logger.log("Found: " + f.getName() + " at row " + indexRow + " at column from " + firstIndexColumn + " to " + lastIndexColumn + ".");

                    //Set variables back
                    firstIndexColumn = 0;
                    lastIndexColumn = 0;
                    className = "";
                    temp = 'x';
                }
                indexRow++;
            }

            Logger.log("Found " + tempCssClass.size() + " classes.");

            if (tempCssClass.size() > 0) {
                this.cssClasses.addAll(tempCssClass);
                Logger.log("This class will be added to 'cssnames'-artifact, in order to be able for later methods.");
            } else if (tempCssClass.size() == 0) {
                this.cssFilesWithoutClasses.add(file.getName());
            }

        } catch (FileNotFoundException e) {
            //Should never happen, expect user deleted files manually.
            Logger.log("FAIL: File not exist. Should definatally exist." + System.lineSeparator() + "Make sure no process deletes any files from 'osm-styles' folder.");
            e.printStackTrace();
        } catch (IOException e) {
            Logger.log("IOException - while trying to search a file for a given key. - Check BufferedReader operations.");
            e.printStackTrace();
        }
    }

    /**
     * Checks if there are any accordances between the given css class
     * and a database table name.
     * <p>
     * Use this method, if program is runned without a second parameter.
     *
     * @param cssClass
     * @return Null if there are no accordances.
     */
    public Match checkForMatch(CssClass cssClass, MyTable table, String cssFile) {

        JaroWinklerSimilarity jws = new JaroWinklerSimilarity();
        Match match = null;
        CssClass temp = null;
        double actualPercentage = 0;
        String suggestedName;

        /**
         * TODO Implement next level comparison
         *
         * To avoid massive calculations, the program first compares the words with
         * the Jarow Winkler similarity algorithm.
         *
         * It compares 2 string.
         * It returns:
         *  1 = same word
         *  0 = completly different words
         *  0.10 ... 0.99
         *
         *  When it finds a good accurancy between two words, but doesnt returns
         *  1, then it trys the JarowWinkler algorithm, while it
         *  extends the cssclass/filenames with this _points, _lines and _polygons.
         *
         */


        DecimalFormat df = new DecimalFormat("#.##");

        if (cssFile == null) {
            actualPercentage = jws.apply(cssClass.getName(), table.getTableName()) * 100;
            Logger.log("CSSClass: " + cssClass.getName() + " ~ Tablename: " + table.getTableName() + " -> Percentage = " + df.format(actualPercentage));

            if (actualPercentage >= this.percentage.doubleValue()) {
                suggestedName = this.getSuggestionForName(cssClass.getName(), table.getTableName(), actualPercentage);
                match = new Match(cssClass, table, false, actualPercentage, suggestedName);
            }

        } else if (cssFile != null) {
            actualPercentage = jws.apply(cssFile, table.getTableName()) * 100;
            Logger.log("CSSFile: " + cssFile + " ~ Tablename: " + table.getTableName() + " -> Percentage = " + df.format(actualPercentage));

            if (actualPercentage >= this.percentage.doubleValue()) {
                //Remove ".css" from filename
                cssFile = cssFile.substring(0, cssFile.length() - 4);
                suggestedName = this.getSuggestionForName(cssFile, table.getTableName(), actualPercentage);
                temp = new CssClass(new File(cssFile), cssFile, -1, -1, -1);
                match = new Match(temp, table, true, actualPercentage, suggestedName);
            }
        }

        this.completePercentage += actualPercentage;

        return match;
    }

    /**
     * Will provide the "best" name for this CSSclass, in connection to the tablename.
     *
     *
     * @param css
     * @param table
     * @return
     */
    public String getSuggestionForName(String css, String table, double percentage){
        JaroWinklerSimilarity jws = new JaroWinklerSimilarity();

        /**
         * Use this enums, which contains identifier for this method.
         * OHDM databasetables have normally "extenstions" like _points, _polygons or _lines.
         */
        CSSIdentifier.POLYGON.getString();
        CSSIdentifier.LINE.getString();
        CSSIdentifier.POINT.getString();

        /**
         * Easy to add new identifiers:
         *      you just need to add a new CSSIdentifier in the Enumclass
         *      and add it to this list.
         *      It will automatically test for this CSSIdentifier with the JarowWinkler algorithm.
         *
         */
        List<CSSIdentifier> identifiers = new ArrayList<>();
        identifiers.add(CSSIdentifier.POLYGON);
        identifiers.add(CSSIdentifier.LINE);
        identifiers.add(CSSIdentifier.POINT);

        List<Double> percentages = new ArrayList<>();

        /**
         * apply with identifiers as suffix like:
         *      highway -> highway_polygons
         */
        for(int i = 0; i < identifiers.size(); i++){
            percentages.add(jws.apply((css + identifiers.get(i).getString()), table));
            System.out.println((css + identifiers.get(i).getString()) + " new name -> " + jws.apply((css + identifiers.get(i).getString()), table));
        }

        /**
         * Get highest:
         */
        int highest = -1;

        for(int i = 0; i < percentages.size(); i++){
            if(percentages.get(i) > percentage){
                highest = i;
            }
        }

        if(highest == -1){
            return css;
        } else {
            Logger.log("New suggestion for CSSClass: " + css + " and Table: " + table + "."
                    + System.lineSeparator() + "Accuracy would increase by " + (percentages.get(highest) - percentage) + " %.");
            return (css + identifiers.get(highest));
        }

        /**
         * What is a good suffix:
         *      when the percentage increased when a identifier suffix was added
         */


        /**
         * Now try to check, if when you do this polygon, line or point as prefix or suffix, maybe the percentage gets higher
         * or will be 100%
         */
    }

    /**
     * Calls the checkAccordance or checkAccordanceWithPercentage- methods
     * depending on how many parameters where given at the programstart,
     * for each rendering database table.
     * <p>
     * Database tables are listed and stored in artifact/table_names.
     */
    public void searchForMatches() {

        Logger.log("Start search for matching CSS Classes and OHDM DB tablenames ...");
        Logger.log("****************************************************************");

        /*
         * TODO:
         *
         * Here is the perfect point, to scan the css files for any rule specified in die
         * CSSIdentifier Class.
         *
         * CSSIdentifier is a Enum class.
         *
         * You can define rules in this class like I did it for the CLASSNAME.
         *
         * A rule is a specified String.
         * The method scanFile(File, Rule), scans in a give (CSS)-File, for your rule.
         *
         * To get better results, it is helpful to implement more rules.
         *
         */

        this.allMatches = new ArrayList<>();
        Match tempMatch;

        for (File f : this.cssFiles) {
            this.scanFile(f, CSSIdentifier.CLASSNAME);
        }

        this.storeCssClasses();


        //For each table, check all CSSClasses:
        for (MyTable table : this.tables) {

            Logger.log("Check for matches with, for table: '" + table.getTableName() + "'");

            for (CssClass cssClass : cssClasses) {
                //this.checkAccordance()
                if ((tempMatch = this.checkForMatch(cssClass, table, null)) != null) {
                    this.allMatches.add(tempMatch);
                    Logger.log("Matches with: '" + cssClass.getName() + "' (class).");
                }
            }

            for (String css : cssFilesWithoutClasses) {
                //this.checkAccordance()
                if ((tempMatch = this.checkForMatch(null, table, css)) != null) {
                    this.allMatches.add(tempMatch);
                    Logger.log("Matches with: '" + css + "' (file).");
                }
            }
        }

        if (this.allMatches.size() > 0) {
            Logger.log("****************************************************************************");
            Logger.log("All matches had an average percentage accuracy of " + this.df.format(completePercentage / allMatches.size()) + " %. Size of Matchlist: " + this.allMatches.size() + ".");
        } else{
            Logger.log("There were no matches found.");
        }

        this.storeMatches();
    }

    /**
     * Stores CSS Classes and CSS File names (when no class was found in a file),
     * in the artifact folder.
     */
    public void storeCssClasses() {
        StorageAdminInterface sa = new StorageAdmin();
        sa.storeList(sa.cssClassToString(this.cssClasses), StorageAdminInterface.CSS_CLASSES, true);
        sa.storeList(this.cssFilesWithoutClasses, StorageAdminInterface.CSS_FILES_WITHOUT_CLASSES, true);
    }

    /**
     * Stores matches, in the artifact folder.
     */
    public void storeMatches() {
        StorageAdminInterface sa = new StorageAdmin();
        sa.storeList(sa.matchesToString(this.allMatches), StorageAdminInterface.MATCHES, true);
    }
}
