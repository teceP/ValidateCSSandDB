package enumerations;

/**
 * Contains several important CSS identifiers
 * @author Mario
 *
 */
public enum CSSIdentifier {
	
	//Its important to let these whitespaces in the enums, because other methods will directly start to scan for the class/type/... value
	/*
	 * TODO:
	 * Should add identifiers like "... and type in ..." to get more precise results
	 */
	
	CLASSNAME("[class = '"),
	TYPE("[type = '"),
	SORTBY(""),
	RULE_EVALUATION(""),
	SD("[@sd "),
	
	/* TODO
	 * 
	 * 2 Options:
	 * 1- Take the next "( .... )" 
	 * 2- if no "(" then directly take next word in "'". Its only one type.
	 * 
	 * delete "(" and ")"
	 * seperate by tokenize with "," as delimeter
	 * delete all "'"
	 * trim string
	 * -> used as css file name like:
	 * highway_
	 */
	AND_TYPE_IN("and type in");
	
	private String identifier;

	CSSIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getString() {
		return this.identifier;
	}

}
