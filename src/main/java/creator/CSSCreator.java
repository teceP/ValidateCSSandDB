package creator;

import java.io.File;

import models.Accordance;

public class CSSCreator implements Runnable {
	
	private Accordance[] accordances;
	
	public CSSCreator(Accordance[] accordances) {
		File folder = new File("output");
		folder.mkdir();
		
		this.accordances = accordances;
	}

	@Override
	public void run() {
		
	}

}
