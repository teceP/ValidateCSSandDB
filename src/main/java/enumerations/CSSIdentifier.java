package enumerations;

/**
 * Contains several important CSS identifiers/rules
 * @author Mario
 *
 */
public enum CSSIdentifier {
	
	//Its important to let these whitespaces in the enums, because other methods will directly start to scan for the class/type/... value

	/*
	 * TODO:
	 * Should add identifiers like "... and type in ..." to get more precise results
	 */

	/**
	 * CSS Rules:
	 */
	CLASSNAME("[class = '"),
	TYPE("[type = '"),
	AND_TYPE_IN("and type in");
	
	private String identifier;

	CSSIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getString() {
		return this.identifier;
	}

}
