package testBase;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class OpenFile {

	public static void main(String[] args) throws IOException, InterruptedException, AWTException {

		File f = new File("C:\\Automation\\ClarityTestFile.xlsx");

		Desktop d = Desktop.getDesktop();
		d.open(f);
		
		System.out.println("File is opened");
		Thread.sleep(3000);
		
		Robot robot =new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyRelease(KeyEvent.VK_A);
		System.out.println("Cobtrol A is pressed");
		
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyRelease(KeyEvent.VK_C);
		
		System.out.println("File is copied");
		
	}

}
