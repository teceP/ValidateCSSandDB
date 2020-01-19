package creator;

import java.io.File;

import models.Match;

public class CSSCreator implements Runnable {
	
	private Match[] accordances;
	
	public CSSCreator(Match[] accordances) {
		File folder = new File("output");
		folder.mkdir();
		
		this.accordances = accordances;
	}

	@Override
	public void run() {
		
	}

}
