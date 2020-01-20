package git;

import java.io.File;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;

import logger.Logger;

public class GitProvider {

	/**
	 * Github repository URL
	 *
	 * Change this, to download another repository with CSS styles.
	 * Make sure, you change the folder name "osm-styles" in the CSSAnalyzer class.
	 *
	 */
	private final String url = "https://github.com/geosolutions-it/osm-styles.git";

	/**
	 * Downloads the latest geosolution Github repository by default.
	 *
	 * @param stylesFolder Destinationfolder of your download
	 */
	public GitProvider(String stylesFolder) {
		File folder = new File(stylesFolder);

		if(folder.exists()) {
			Logger.log("Git dir already exists. Deleting old dir now ...");
			this.cleanFolder(folder);
		}
	}

	/**
	 * If there exists any folder with the same name of the repo, it will delete all files
	 * and directorys.
	 *
	 * @param folder Repofolder/directory
	 */
	private void cleanFolder(File folder) {
		
		try {
		File mainFolder = new File("osm-styles");
	    for (File subFile : mainFolder.listFiles()) {
	         if(subFile.isDirectory()) {
	            cleanFolder(subFile);
	         } else {
	            subFile.delete();
	         }
	      }
	      folder.delete();
		}catch (StackOverflowError soe) {
			Logger.log("Stackoverflow while trying to delete old styles folder." + System.lineSeparator() + "Please delete 'osm-styles' manually");
			System.exit(1);
		}
	}

	/**
	 * Downloads data from git repo.
	 *
	 * @return True if repo was downloaded without exceptions.
	 */
	public boolean getData() {
		Logger.log("Cloning Repository '" + url + "' ...");
		
		try {
			Git.cloneRepository().setURI(url).call();
			Logger.log("Cloning completed.");
			return true;
		} catch (GitAPIException e) {
			Logger.log("Cloning failed.");
			e.printStackTrace();
		} catch (JGitInternalException gitE) {
			Logger.log("Cloning failed.");
			Logger.log("FAIL: Try to delete the folder 'osm-styles' manually.");
			System.exit(1);
		}
		return false;
	}
}
