package storage;

import models.CssClass;
import models.Match;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public interface StorageAdminInterface {

    /**
     * Delimeter for String items
     */
    String DELIMETER = "#";

    /**
     * Delimeter for attributes of an object.
     */
    String ATTRIBUT_DELIMETER = "~";

    final String ARTIFACT = "artifact/";

    //Filenames
    String TABLE_NAMES = "table_names";
    String CSS_FILES = "css_files";
    String OSM_STYLES_FOLDER_PREFIX = "osm-styles/css/";
    String CSS_CLASSES = "css_classes";
    String CSS_FILES_WITHOUT_CLASSES = "css_without_classes";
    String MATCHES = "matches";
    public void storeList(List<String> list, String file, boolean artifact);

    /**
     * Restores a list from a given filename.
     * Searches in folder and subfolders for this file.
     *
     * @param filename
     * @return
     */
    public List<String> restoreList(String filename, boolean artifact);


    public abstract List<String> cssClassToString(List<CssClass> list);

    public abstract List<CssClass> stringToCssClass();

    public List<String> matchesToString(List<Match> list);

    public List<Match> stringToMatches();
}
