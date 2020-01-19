package analyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import git.GitProvider;
import logger.Logger;
import storage.StorageAdmin;
import storage.StorageAdminInterface;

public class CSSAnalyzer implements Runnable {

    /**
     * GITHUB Cloned folder. NOT css style folder!
     */
    private String stylesFolder;

    /**
     * Css style folder. Cloned from Github.
     */
    private String cssFolder;

    /**
     * All css files/paths which are in the "osm-styles" folder are safed in this list.
     */
    private List<File> files;

    /**
     * Runs the GitProvider, which will download the latest git repo with css-styles.
     * Will collect all CSS Files and store it to the artifact folder.
     */
    public CSSAnalyzer() {
        files = new ArrayList<File>();
        this.stylesFolder = "osm-styles";
        this.cssFolder = "osm-styles/css";
    }

    /**
     * Runs the GitProvider.
     */
    private boolean downloadFiles() {

        File osmStyles = new File("osm-styles");
        if (osmStyles.exists()) {
        	Logger.log("Git repo only exists on your machine. Delte 'osm-styles'-folder, in order to auto-download the newest styles." + System.lineSeparator() + "Program uses old styles and continues now.");
            return true;
        } else {
            GitProvider git = new GitProvider(stylesFolder);

            if (git.getData()) {
                this.getFiles();
            } else {
                Logger.log("Could not download css styles. Exit program.");
				return false;
            }
            return true;
        }
    }

    /**
     * Collects all CSS files, which where downloaded in a list.
     */
    private void getFiles() {
        Logger.log("*******************************");
        Logger.log("Start getting CSS files from folder '" + cssFolder + "'.");
        Logger.log("*******************************");

        try (Stream<Path> path = Files.walk(Paths.get(cssFolder))) {

            this.files = path.filter(Files::isRegularFile).map(x -> new File(x.toString())).collect(Collectors.toList());

            for (File f : this.files) {
                Logger.log(f.getName());
            }

            this.storeDate();

        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("FAIL: Getting file paths..");
            Logger.log("*******************************");
        }
        Logger.log("Loaded all files. (" + files.size() + " files)");
        Logger.log("**********************************");
    }

    /*
     * Stores the cssfile-list in an artifact.
     */
    public void storeDate() {

        StorageAdminInterface sa = new StorageAdmin();
        List<String> s = new ArrayList<>();

        for (File f : this.files) {
            s.add(f.getName());
        }

        sa.storeList(s, StorageAdminInterface.CSS_FILES, true);
    }

    @Override
    public void run() {
        this.downloadFiles();
    }
}