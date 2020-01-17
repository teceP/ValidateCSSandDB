package models;

import java.io.File;

public class Found {
	
	/**
	 * File which where operated in.
	 */
	private File file;
	
	/**
	 * Name of a CSS class
	 */
	private String name;
	
	/**
	 * First character position/index of the CSS class name (ROW)
	 */
	private int characterIndexRow;
	
	/**
	 * First character position/index of the CSS class name (COLUMN)
	 */
	private int characterIndexColumn;
	
	/**
	 * Length of the CSS class name.
	 * Example:
	 * [highway]
	 * 012345678
	 * length = 8
	 * 
	 */
	private int length;
	
	public Found(File file, String name, int characterIndexRow, int characterIndexColumn, int length) {
		this.file = file;
		this.name = name;
		this.characterIndexRow = characterIndexRow;
		this.characterIndexColumn = characterIndexColumn;
		this.length = length;
	}

	public String getName() {
		return name;
	}

	public int getCharacterIndexRow() {
		return characterIndexRow;
	}

	public int getCharacterIndexColumn() {
		return characterIndexColumn;
	}

	public int getLength() {
		return length;
	}

	public File getFile() {
		return file;
	}
	
	

	
}
