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
        tables = new ArrayList<>();

        List<String> tabs = sa.restoreList(StorageAdminInterface.TABLE_NAMES, true);
        MyTable temp;
        for (String s : tabs) {
            temp = new MyTable(s);
            this.tables.add(temp);
        }
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

        // if no match => check for contains and then for "contains_point"||contains_polygon"...

        //herausfinden, ab wann nur noch _point, _line oder _polygon fehlt (% bis zu 100)
        //ab dieser % zahl dann _line, _point oder _polygon hinzufügen
        //=>> weiter: speichern, wie die datei heißen soll , in object match

        DecimalFormat df = new DecimalFormat("#.##");

        if (cssFile == null) {
            actualPercentage = jws.apply(cssClass.getName(), table.getTableName()) * 100;
            Logger.log("CSSClass: " + cssClass.getName() + " ~ Tablename: " + table.getTableName() + " -> Percentage = " + df.format(actualPercentage));

            if (actualPercentage >= this.percentage.doubleValue()) {
                match = new Match(cssClass, table, false, null);
            }

        } else if (cssFile != null) {
            actualPercentage = jws.apply(cssFile, table.getTableName()) * 100;
            Logger.log("CSSFile: " + cssFile + " ~ Tablename: " + table.getTableName() + " -> Percentage = " + df.format(actualPercentage));

            if (actualPercentage >= this.percentage.doubleValue()) {
                //Remove ".css" from filename
                cssFile = cssFile.substring(0, cssFile.length() - 4);
                temp = new CssClass(new File(cssFile), cssFile, -1, -1, -1);
                match = new Match(temp, table, true, null);
            }
        }

        this.completePercentage += actualPercentage;

        return match;
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
            Logger.log("All matches had an average percentage accuracy of " + completePercentage / allMatches.size() + "%.");
        } else{
            Logger.log("There were no match found.");
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
        System.out.println("Size of Matchlist: " + this.allMatches.size());
        sa.storeList(sa.matchesToString(this.allMatches), StorageAdminInterface.MATCHES, true);
    }
}
