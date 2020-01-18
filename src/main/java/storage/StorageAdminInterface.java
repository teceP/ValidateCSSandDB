package storage;

import java.io.FileNotFoundException;
import java.util.List;

public interface StorageAdminInterface {

    final String ARTIFACT = "artifact/";

    //Filenames
    String TABLE_NAMES = "tablenames";
    String CSS_FILES = "cssfiles";
    String OSM_STYLES_FOLDER_PREFIX = "osm-styles/css/";

    public void storeList(List<String> list, String file, boolean artifact);

    /**
     * Restores a list from a given filename.
     * Searches in folder and subfolders for this file.
     *
     * @param filename
     * @return
     */
    public List<String> restoreList(String filename, boolean artifact) throws FileNotFoundException;
}
