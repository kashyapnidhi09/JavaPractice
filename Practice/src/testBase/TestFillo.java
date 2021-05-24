package testBase;
import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

public class TestFillo {

	public static void main(String[] args) throws FilloException {
		// TODO Auto-generated method stub
		
		String partCode = null;
		String testdataPath = "C:\\Users\\nkashyap\\DesktopAutomation\\DesktopAUT\\src\\testData\\RAT_TestData\\excel\\RAT_TestData.xlsx";
		String sheetName="TestData";
		String testcasename="TestCase1";

		Fillo fillo=new Fillo();
		Connection conn=fillo.getConnection(testdataPath);
		String query=String.format("SELECT * FROM %s Where TestCaseName='%s'",sheetName,testcasename);
		Recordset recordset=conn.executeQuery(query);
		while(recordset.next())
		{
			partCode=recordset.getField("responseFileCode");
		}
        recordset.close();
		conn.close(); 

		
		System.out.print("part code value is ======= "+partCode);
	}

}
