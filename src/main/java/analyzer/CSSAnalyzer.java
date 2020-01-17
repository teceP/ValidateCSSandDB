package analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.w3c.css.sac.InputSource;


import creator.CSSCreator;
import git.GitProvider;
import logger.Logger;

public class CSSAnalyzer implements CSSAnalyzeInterface, Runnable{

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

	public CSSAnalyzer() {	
		files = new ArrayList<File>();
		this.stylesFolder = "osm-styles";
		this.cssFolder = "osm-styles/css";
	}


	private void downloadFiles() {
		GitProvider git = new GitProvider(stylesFolder);
		
		if(git.getData()) {
			this.getFiles();
		}else {
			Logger.log("Could not download css styles. Exit program.");
		}
	}

	private void getFiles() {
		Logger.log("*******************************");
		Logger.log("Start getting CSS files from folder '" + cssFolder + "'.");
		Logger.log("*******************************");

		try (Stream<Path> path = Files.walk(Paths.get(cssFolder))) {

			this.files = path.filter(Files::isRegularFile).map(x -> new File(x.toString())).collect(Collectors.toList());

			for (File f : this.files) {
				Logger.log(f.getName());
			}

		} catch (IOException e) {
			e.printStackTrace();
			Logger.log("FAIL: Getting file paths..");
			Logger.log("*******************************");
		}
		Logger.log("Loaded all files. (" + files.size() + " files)");
		Logger.log("**********************************");
	}

	/**
	 * Creates array out of files.
	 * 
	 * @return
	 */
	public File[] getFileArray(){
		//No need to use list anymore, because we dont want to add more file later.
		
		File[] fileArray = new File[this.files.size()];
		
		for(int i = 0; i < this.files.size(); i++) {
			fileArray[i] = this.files.get(i);
		}
		
		return fileArray;
	}

	@Override
	public void run() {
		this.downloadFiles();
	}
}