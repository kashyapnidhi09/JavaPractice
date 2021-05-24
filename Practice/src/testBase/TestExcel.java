package testBase;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class TestExcel {

	@Test(dataProvider = "readexceldata")
	public void testExcel(Map<String,String> TestDataInMap ) {

		String execute =  TestDataInMap.get("RUN");
		System.out.println(execute);
		ExcelUtil.checkTestRunCondition(execute);

		String partCode =TestDataInMap.get("PartCode");
		System.out.println("part code ------   " + partCode);

		String description = TestDataInMap.get("Description");
		System.out.println("description ------   " + description);
	}

	@DataProvider(name = "readexceldata")
	public static  Object[][] passData() throws Exception {

		// Map<String, String> TestDataInMap=ExcelUtil.getTestDatainMapByTestCaseName();
		// return TestDataInMap;

		File file = new File(
				"C:\\Users\\nkashyap\\RATAutomationWorkspace\\DesktopAppAUT\\RAT_TestData.xlsx");
		FileInputStream fis = new FileInputStream(file);

		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0);
		//wb.close();
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		int lastCellNum = sheet.getRow(0).getLastCellNum();
		System.out.println(lastCellNum);
		Object[][] obj = new Object[lastRowNum][1];

		for (int i = 0; i < lastRowNum; i++) {
			Map<Object, Object> datamap = new HashMap<>();
			for (int j = 0; j < lastCellNum; j++) {
				datamap.put(sheet.getRow(0).getCell(j).toString(), sheet.getRow(i + 1).getCell(j).toString());
			}
			obj[i][0] = datamap;

		}
		return obj;
	}

}
