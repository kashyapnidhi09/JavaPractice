package testBase;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

public class ConvertExcelToMap {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		StringWriter sw = new StringWriter();
		ExcelCSVPrinter excelPrinter = new ExcelCSVPrinter(sw);
		excelPrinter.changeDelimiter('\t');
		File f = new File("C:\\automation\\Mailings.xlsx");
		FileInputStream fin = new FileInputStream(f);
		XSSFWorkbook wb = new XSSFWorkbook(fin);
		XSSFSheet sheet = wb.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		int lastCellNum = sheet.getRow(0).getLastCellNum();

		System.out.println(lastRowNum);

		System.out.println(lastCellNum);
		List<List<String>> rowList = new ArrayList<List<String>>();
		List<String> colHeader = new ArrayList<String>();
		List<String> rowData = new ArrayList<String>();
		Iterator<Row> rowIterator = sheet.iterator();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();

				if (row.getRowNum()> 0) { // To filter column headings

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						colHeader.add(cell.getNumericCellValue() + "");
						break;
					case Cell.CELL_TYPE_STRING:
						colHeader.add(cell.getStringCellValue());
						break;
					}
				}
			}
		}
		System.out.println(colHeader);
		 excelPrinter.writeln(colHeader.toString());
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(sw.toString());
		clipboard.setContents(strSel, strSel);
		System.out.println("String is copied on clipboard");

	}	
}
