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

	private boolean isFile;


	/**
	 *
	 * @param cssClass
	 * @param table
	 */
	public Match(CssClass cssClass, MyTable table, boolean isFile, Double percentage) {
		this.isFile = isFile;
		this.cssClass = cssClass;
		this.table = table;
		this.percentage = percentage;
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
}
