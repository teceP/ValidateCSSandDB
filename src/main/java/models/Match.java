package models;


public class Match {

	/**
	 * CSS identifier
	 */
	private CssClass cssClass;

	/**
	 * Database table
	 */
	private MyTable table;

	/**
	 * Found with percentage value
	 */
	private Double percentage;

	/**
	 * Match was found within a CSS file OR with the name of the CSS file
	 *
	 * If true: match was found with the CSS filename
	 */
	private boolean isFile;

	/**
	 * The created CSSFile should be named like this
	 */
	private String suggestedName;


	/**
	 * Represents a match between a tablename and a CSS Class or a CSS File
	 * @param cssClass
	 * @param table
	 */
	public Match(CssClass cssClass, MyTable table, boolean isFile, Double percentage, String suggestedName) {
		this.isFile = isFile;
		this.cssClass = cssClass;
		this.table = table;
		this.percentage = percentage;
		this.suggestedName = suggestedName;
	}


	public CssClass getCssClass() {
		return cssClass;
	}

	public MyTable getTable() {
		return table;
	}

	public String getTableName() {
		return table.getTableName();
	}

	public Double getPercentage() {
		return percentage;
	}

	public boolean isFile() {
		return isFile;
	}

	public String getSuggestedName() {
		return this.suggestedName;
	}
}
