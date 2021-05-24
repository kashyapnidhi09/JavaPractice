package testBase;

import java.io.StringWriter;
import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;

public class CopyToClipboard {

	public static void main(String[] args) {
		String str = "String destined for clipboard in notepad and excel";

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(str);
		clipboard.setContents(strSel, strSel);
		System.out.println("String is copied on clipboard");

	}

}
