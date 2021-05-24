package testBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateNewFile {
	
	public static void main(String[] args) throws IOException {
		
		Path path = Paths.get("C:\\Automation\\javaprogram.txt"); //creates Path instance  
		  
		Path p= Files.createFile(path);     //creates file at specified location  
		System.out.println("File Created at Path: "+p);  
		File f=p.toFile();
		System.out.println("File created to file type");
		if (f.exists())
		{
			f.delete();
			
		}
	}
	}


