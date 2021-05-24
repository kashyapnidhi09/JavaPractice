package testBase;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.Ostermiller.util.ExcelCSVPrinter;

class CopyExceltoclipboard {

	public static void main(String[] args) throws Exception {		
		
		
		StringWriter sw=new StringWriter();
		ExcelCSVPrinter excelPrinter = new ExcelCSVPrinter(sw);
		excelPrinter.changeDelimiter('\t');
		// List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
		 
		 ArrayList<ArrayList<String>> OUT = new ArrayList<ArrayList<String>>();
		 File f=new File("C:\\automation\\Cliprd.xlsx");
		 FileInputStream fin=new FileInputStream(f);
		 XSSFWorkbook wb = new XSSFWorkbook(fin);
		 XSSFSheet sheet = wb.getSheetAt(0);
		  
		  int lastRowNum = sheet.getLastRowNum(); int lastCellNum =
		  sheet.getRow(0).getLastCellNum();
		  
		  Iterator<Row> rowIterator = sheet.iterator();
		  
		  // Traversing over each row of XLSX file 
		  int count=1;
		  while(rowIterator.hasNext())
		  { 
			  Row row = rowIterator.next(); 
		  ArrayList<String>
		  InnerArray = new ArrayList<String>() ;
		  boolean debug=true;
		  if(debug)System.out.print(count + ". \t"); // For each row, iterate through
		  each columns Iterator<Cell> cellIterator = row.cellIterator();
		  
		  while (cellIterator.hasNext()) {
		  
		  Cell cell = cellIterator.next();
		  
		  switch (cell.getCellType()) { case Cell.CELL_TYPE_STRING: String c =
		  cell.getStringCellValue(); if(debug)System.out.print(c + "\t");
		  InnerArray.add(c); break; case Cell.CELL_TYPE_NUMERIC: int n = (int)
		  cell.getNumericCellValue(); if(debug)System.out.print(n + "\t");
		  InnerArray.add(String.valueOf(n)); break; case Cell.CELL_TYPE_BOOLEAN:
		  boolean b = cell.getBooleanCellValue(); if(debug)System.out.print(b + "\t");
		  InnerArray.add(String.valueOf(b)); break; default : } }
		  if(debug)System.out.println(""); OUT.add(InnerArray); count++; }
		 
			 
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			StringSelection strSel = new StringSelection(sw.toString());
			clipboard.setContents(strSel, null);
			System.out.println("String is copied on clipboard");
			
			 
		
		
		
		
		
	}

	public static Object[][] exceltoMAp() throws Exception {
		File f = new File("C:\\automation\\Cliprd.xlsx");
		FileInputStream fin = new FileInputStream(f);
		XSSFWorkbook wb = new XSSFWorkbook(fin);
		XSSFSheet sheet = wb.getSheetAt(0);
		wb.close();
		int lastRowNum = sheet.getLastRowNum();
		int lastCellNum = sheet.getRow(0).getLastCellNum();
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
