package testBase;

import org.testng.annotations.Test;

public class TestParametrization {
  @Test
  public void f() {
	  
	 String username=System.getProperty("user.name");
	  String ToolkitServer="Deckers";
			  String ROMSAppPath="\\AppData\\Local\\Programs\\Abacus_Toolkits\\Toolkit";
			  
			  
			 String path="C:\\Users\\"+username+ROMSAppPath+" "+ToolkitServer;
			 System.out.println(path);
  }
}
