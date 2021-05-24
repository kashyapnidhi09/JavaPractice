package testBase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class CheckFileCreateNew {

	public static void main(String[] args) throws IOException {

		File f = new File("C:\\Automation\\javaprogram.txt");
		if (!f.exists()) {
			System.out.println("File does not exist");
			f.createNewFile();
			System.out.println(" new file created");
		} else {
			System.out.println("File already exists");
			f.delete();
			System.out.println("File deleted");
			f.createNewFile();
			System.out.println("new file created after delete");
		}
		
		
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			System.out.println("Last Modified Date: " + sdf.format(f.lastModified()));
		
	}

}
