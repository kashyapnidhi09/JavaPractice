package testBase;
import java.util.Map;
import java.util.TreeMap;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

public class FilloinMap {

	public static void main(String[] args) throws FilloException {
		// TODO Auto-generated method stub
		
		
		String testdataPath = "C:\\Users\\nkashyap\\DesktopAutomation\\DesktopAUT\\src\\testData\\RAT_TestData\\excel\\RAT_TestData.xlsx";
		String sheetName="TestData";
		String testcaseName="TestCase1";
		
		Map<String, String> TestDataInMap = new TreeMap<String,String>();
		String query=String.format("SELECT * FROM %s WHERE TestCaseName='%s'",
				sheetName,testcaseName);
	
		Fillo fillo=new Fillo();
		Connection conn=fillo.getConnection(testdataPath);
		Recordset recordset=conn.executeQuery(query);
		while(recordset.next())
		{
			for(String field:recordset.getFieldNames())
			{
				TestDataInMap.put(field, recordset.getField(field));
			}
		}
		conn.close();

		String partCode = TestDataInMap.get("PartCode");
		System.out.println("part code =====   " +partCode);
		
	}

}
