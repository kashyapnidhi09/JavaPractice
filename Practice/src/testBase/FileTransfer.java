package testBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

public class FileTransfer {

	public static void main(String[] args) throws IOException {
		String tm=uploadfile();
		System.out.println("Actual "+tm);
		String date=convertTimestamp(tm);
		System.out.println("date "+date);
		
		
	}
	
	public static String convertTimestamp(String timestamp)
	{
		  DateTimeFormatter formatter=DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
		  LocalDateTime startLocalTime = LocalDateTime.parse(timestamp, formatter);
		  String formattedexpectedtimestamp = startLocalTime.format(formatter);
		  System.out.println("expected time : "+formattedexpectedtimestamp);
		  
	        LocalDateTime dateminusfive =  (Instant.now()
	           .atZone( ZoneId.of ( "America/Denver" ))).toLocalDateTime();
	        
	        DateTimeFormatter dateminusfiveformat = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");	      
	        String formattedStringfive = dateminusfive.format(dateminusfiveformat);
	        System.out.println("Minus 5 : "+formattedStringfive);
	        
	       
	        return formattedStringfive;
	       
				 
	}
	
	public static String uploadfile() throws IOException
	{

		File f = new File("C:\\Automation\\testfilecache.cmm");
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
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		System.out.println("Last Modified Date: " + sdf.format(f.lastModified()));
		String timestampCurrent=sdf.format(f.lastModified());

		SSHClient ssh = new SSHClient();
		ssh.addHostKeyVerifier(new PromiscuousVerifier());
		ssh.connect("eckley");
		System.out.println("Connected to server");
		String username = System.getProperty("user.name");
		String pass = "Torres!@12345";
		String dir = "/prod/prev_use";
		String filePath = "C:\\Automation";
		String filename = "testfilecache.cmm";

		System.out.println(username);
		ssh.authPassword(username, pass);
		System.out.println("user name is verified");
		SFTPClient sftp = ssh.newSFTPClient();

		Path runtimePropFilePath = Paths.get(filePath, filename);
		File file = runtimePropFilePath.toFile();

		
		
		 String rppRemotePath = dir + "/" + file.getName();
	        if ( sftp.statExistence( rppRemotePath ) != null )
	        {
	        	sftp.rm( rppRemotePath );
	        	System.out.println("File deleted");
	        }
		sftp.put(new FileSystemFile(file), dir);
		System.out.println("File uploaded");

		sftp.close();

		ssh.disconnect();
		
		return timestampCurrent;
	}

}
