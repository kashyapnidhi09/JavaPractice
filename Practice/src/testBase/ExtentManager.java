package testBase;

import java.io.File;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
	
	
	public static ExtentReports extent;
	
	public static ExtentReports createInstance()
	{
		//String filename=getReportName();
		/*
		 * String directory=System.getProperty("user.dir")+"/reports/"; new
		 * File(directory).mkdirs();
		 */
		//String path= directory+filename;
		ExtentHtmlReporter htmlReporter=new ExtentHtmlReporter("ExtentReport.html");
		/*
		 * htmlReporter.config().setEncoding("utf-8");
		 * htmlReporter.config().setDocumentTitle("Automation Report");
		 * htmlReporter.config().setReportName("Automation Test Results");
		 * htmlReporter.config().setTheme(Theme.STANDARD);
		 */
		
		extent=new ExtentReports();
		//extent.setSystemInfo("Browser", "Chrome");
		extent.attachReporter(htmlReporter);
		
		return extent;
	}
	
	
	public static String getReportName()
	{
		
		Date d=new Date();
		String fileName= "AutomationReport_"+d.toString().replace(":", "_").replace(" ", "_")+".html";
		return fileName;
	}
	

}
