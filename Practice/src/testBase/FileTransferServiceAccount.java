package testBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.xfer.FileSystemFile;

public class FileTransferServiceAccount {

	public static void main(String[] args) throws IOException {
		String tm = uploadfile();
		System.out.println("Actual " + tm);
		String date = convertTimestamp(tm);
		System.out.println("date " + date);

	}

	public static String convertTimestamp(String timestamp) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
		LocalDateTime startLocalTime = LocalDateTime.parse(timestamp, formatter);
		String formattedexpectedtimestamp = startLocalTime.format(formatter);
		System.out.println("expected time : " + formattedexpectedtimestamp);

		LocalDateTime dateminusfive = (Instant.now().atZone(ZoneId.of("America/Denver"))).toLocalDateTime();

		DateTimeFormatter dateminusfiveformat = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
		String formattedStringfive = dateminusfive.format(dateminusfiveformat);
		System.out.println("Minus 5 : " + formattedStringfive);

		return formattedStringfive;

	}

	public static String uploadfile() throws IOException {

		File f = new File("C:\\automation\\testfilecache.cmm");
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
		String timestampCurrent = sdf.format(f.lastModified());
		SSHClient ssh = new SSHClient();
		ssh.addHostKeyVerifier(new PromiscuousVerifier());
		ssh.connect("qc1udatawas02");
		System.out.println("Connected to server");
		String unixServiceAccount = "svi_dataqaauto";
		String privateKeyDir = "C:\\automation\\privatekey";
		String privatekeyfilname = "svi_dataqaauto_private_rsa";
		
		Path privateKeyPath = Paths.get(privateKeyDir, privatekeyfilname);
		System.out.println("setting up private key path");
		
		  if (!Files.exists(privateKeyPath)) { throw new RuntimeException(
		  "Failed to find ssh private key.  This will prevent logging in.  Your environment is not configured correctly.  Looking for: "
		  + privateKeyPath); }
		 
		ssh.authPublickey(unixServiceAccount, privateKeyPath.toString());
		System.out.println("auth public key verified");
		
		String dir = "/prod/prev_use";
		String filePath = "C:\\automation";
		String filename = "qc02testfilecache.cmm";
		System.out.println("user name is verified");
		SFTPClient sftp = ssh.newSFTPClient();

		Path runtimePropFilePath = Paths.get(filePath, filename);
		File file = runtimePropFilePath.toFile();

		String rppRemotePath = dir + "/" + file.getName();
		if (sftp.statExistence(rppRemotePath) != null) {
			sftp.rm(rppRemotePath);
			System.out.println("File deleted");
		}
		sftp.put(new FileSystemFile(file), dir);
		System.out.println("File uploaded");

		sftp.close();

		ssh.disconnect();

		return timestampCurrent;
	}

}
