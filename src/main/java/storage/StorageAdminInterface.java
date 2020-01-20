package storage;

import models.CssClass;
import models.Match;
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

    /**
     * Foldername, where all the artifacts will be stored
     */
    String ARTIFACT = "artifact/";

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
    List<String> restoreList(String filename, boolean artifact);

    List<String> cssClassToString(List<CssClass> list);

    List<CssClass> stringToCssClass();

    List<String> matchesToString(List<Match> list);

    List<Match> stringToMatches();
}
