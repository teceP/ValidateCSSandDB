package enumerations;

/**
 * Contains several CSS identifiers/rules
 *
 * You can use this in the FileAnalyzer, to scan the files for this identifiers.
 *
 * @author Mario Teklic
 *
 */
public enum CSSIdentifier {

	/*
	 * TODO:
	 * Should add identifiers like "... and type in ..." to get more precise results
	 * If you add new identifiers, you also need to scan for them.
	 * For this you need the checkForMatches()-method, which calls the scanFile(...) method.
	 * Both methods are in the FileAnalyzer.
	 *
	 */


	CLASSNAME("[class = '"),
	TYPE("[type = '"),
	AND_TYPE_IN("and type in");

	/**
	 * Identifier
	 */
	private String identifier;

	CSSIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Returns the identifier
	 * @return
	 */
	public String getString() {
		return this.identifier;
	}

}
