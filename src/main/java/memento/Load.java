package memento;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Load {
	
	private final static String DELIMETER = "*";
	private final static String FILE = "tmp";
	
	public static String[] load() {
		
		try {
			FileReader in = new FileReader(FILE);
			BufferedReader br = new BufferedReader(in);
			StringBuilder line = new StringBuilder();
			
			line.append(br.readLine());
			while(line != null) {
				line.append(br.readLine());
			}
			
			return line.toString().split(DELIMETER);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;	
	}

}
