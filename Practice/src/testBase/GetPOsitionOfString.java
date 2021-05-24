package testBase;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GetPOsitionOfString {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String excelPath = "C:\\Users\\nkashyap\\RATAutomationWorkspace\\DesktopAppAUT\\src\\application\\ROMS\\testdata\\Datafile.xlsx";
		FileInputStream fileInputStream = new FileInputStream(excelPath);

		Workbook wb = new XSSFWorkbook(fileInputStream);
		Sheet sheet = wb.getSheetAt(0);		
		System.out.println("in main method");
		List<Integer> position=new ArrayList<Integer>();
		position=getRowNumColumnNumofExecuteCell(sheet,"FastPathRB");
		System.out.println("POI method called");
		System.out.println(position);
		String executeValue=readExcuteCellValue(sheet,position);
		System.out.println("execute Value is ="+executeValue);
		
		/*
		 * int colNum, rowNum,colNumExecuteField;
		 * 
		 * 
		 * for (Row row : sheet) { for (Cell cell : row) { if (cell.getCellType() ==
		 * Cell.CELL_TYPE_STRING) { if (cell.getStringCellValue().equals("FastPathRB"))
		 * {
		 * 
		 * rowNum=row.getRowNum(); System.out.println("row num of string "+rowNum);
		 * colNum=cell.getColumnIndex(); System.out.println("Column num = "+colNum);
		 * 
		 * colNumExecuteField=colNum+1;
		 * System.out.println("Col num of execute field of filed name = "
		 * +colNumExecuteField); cell.getStringCellValue(); } } } }
		 */
		  
			 
		 }
	
	public static List<Integer> getRowNumColumnNumofExecuteCell(Sheet sheet, String fieldName)
	{
		int colNum, rowNum=-1;
		int colNumExecuteField=-1;;
		List<Integer> position=new ArrayList<Integer>();
		for (Row row : sheet) {
			 for (Cell cell : row) {
		            if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
		                if (cell.getStringCellValue().equals(fieldName)) {
		                	
		                	rowNum=row.getRowNum();
		                    System.out.println("row num of string "+rowNum); 
		                    colNum=cell.getColumnIndex();
		                    System.out.println("Column num = "+colNum);		                    
		                    colNumExecuteField=colNum+1;
		                    System.out.println("Col num of execute field of filed name = "+colNumExecuteField);
		                 
		                }
		            }
		        }
		    }               
		  position.add(rowNum);
		  position.add(colNumExecuteField);
		return position;
	}
	
	public static String readExcuteCellValue(Sheet sheet, List<Integer> position)
	{
		
		Row row=sheet.getRow(position.get(0));  //row  
		Cell cell=row.getCell(position.get(1)); //col 1
		String value=cell.getStringCellValue(); 
		return value;
		
	}

	public static String getObjectMapping()// row num, sheet
	{
		String objectMapping="";
		
		
		return objectMapping;
	}

	}


