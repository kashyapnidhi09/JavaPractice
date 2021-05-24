package testBase;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.Ostermiller.util.ExcelCSVPrinter;

public class CopyDateFromExcel {

	public static void main(String[] args) throws IOException {

		StringWriter sw = new StringWriter();
		ExcelCSVPrinter excelPrinter = new ExcelCSVPrinter(sw);
		excelPrinter.changeDelimiter('\t');
		File f = new File("C:\\automation\\Mailings.xlsx");
		FileInputStream fin = new FileInputStream(f);
		XSSFWorkbook wb = new XSSFWorkbook(fin);
		XSSFSheet sheet = wb.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		int lastCellNum = sheet.getRow(0).getLastCellNum();
		//Iterator<Row> rowIterator = sheet.iterator();
		List<Map<String,String>> resultList = new ArrayList<Map<String, String>>();
		Map<String, String> headerMap = new HashMap<>();
		
			for (int i = 0; i < 1; i++) {
			
			for (int j = 0; j < lastCellNum; j++) {
				headerMap.put(sheet.getRow(0).getCell(j).toString(), sheet.getRow(i + 1).getCell(j).toString());
			}
			}
			
			HashMap<String, String> headers = new HashMap<String, String>();
	        HashMap<Object, String> cells = new HashMap<Object, String>();
	        Iterator<Row> rowIterator = sheet.iterator();
	        if (rowIterator.hasNext()) {
	            Row row = rowIterator.next();
	            // For each row, iterate through all the columns
	            Iterator<Cell> cellIterator = row.cellIterator();
	            int colNum = 0;
	            while (cellIterator.hasNext()) {
	                Cell cell = cellIterator.next();
	                headers.put(colNum++, cell.getStringCellValue());
	            }
	            
	            
	     resultList.add(headers);       
		excelPrinter.writeln(values);
		
		 excelPrinter.flush();
	     excelPrinter.close();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(sw.toString());
		clipboard.setContents(strSel, strSel);
		System.out.println("String is copied on clipboard");

	}

}
