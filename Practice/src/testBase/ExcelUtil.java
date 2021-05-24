package testBase;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.SkipException;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

public class ExcelUtil {
	
	
	public static Map<String, String> getTestDatainMapByTestCaseName() throws FilloException 
	{
		String testdataPath = "C:\\Users\\nkashyap\\RATAutomationWorkspace\\DesktopAppAUT\\RAT_TestData.xlsx";
		String sheetName="TestData";
		String testcaseName="TestCase1";
		
		Map<String, String> TestDataInMap = new TreeMap<String,String>();
		String query=String.format("SELECT * FROM %s WHERE TestCaseName='%s'",
				sheetName,testcaseName);
	
		System.out.println(query);
		Fillo fillo=new Fillo();
		Connection conn=null;
		Recordset recordset=null;
		conn=fillo.getConnection(testdataPath);
		recordset=conn.executeQuery(query);
		while(recordset.next())
		{
			for(String field:recordset.getFieldNames())
			{
				TestDataInMap.put(field, recordset.getField(field));
			}
		}
		conn.close();
		return TestDataInMap;
	}
	
	public static int getLastRow() throws Exception
	{
		
		File file = new File("C:\\Users\\nkashyap\\DesktopAutomation\\DesktopAUT\\src\\testData\\RAT_TestData\\excel\\RAT_TestData.xlsx");
	    FileInputStream fis = new FileInputStream(file);

	    XSSFWorkbook wb = new XSSFWorkbook(fis);
	    XSSFSheet sheet = wb.getSheetAt(0);
	    wb.close();
	    int lastRowNum = sheet.getLastRowNum() ;
	    return lastRowNum;
	    
	    
	}
	
	public static void checkTestRunCondition(String execute)
	{
		if (execute.equals("No")) {
			System.out.println("inside if");
			throw new SkipException("Test case skipped since Run flag is set to No");
		}
	}
	

}
