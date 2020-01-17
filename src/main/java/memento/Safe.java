package memento;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import logger.Logger;
import models.MyTable;

public class Safe {

	private final static String DELIMETER = "*";
	private final static String FILE = "tmp";

	public static void safe(List<MyTable> content) {
		
		File f = new File(FILE);
		
		if(f.exists()) {
			f.delete();
		}
		
		try {
			PrintWriter writer = new PrintWriter(FILE, "UTF-8");
			
			StringBuilder sb = new StringBuilder();
			
			for(MyTable s : content) {
				sb.append(s.getTableName());
				sb.append(DELIMETER);
			}
			
			writer.write(sb.toString());
			writer.flush();
			writer.close();
			
		} catch (FileNotFoundException e) {
			Logger.log("Tempfile not found. Make sure, no other process blocks or deletes this file.");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Logger.log("Tempfile encoding exception.");
			e.printStackTrace();
		}
	}
}
