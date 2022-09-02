package ru.gazpromneft.gfemproto;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
/**
* Creation-Time: 14:57
* Creation-Date: 02/09/2022
* Author: Terekhin Rodion
* Author-Email: rodionterekhin@gmail.com
*
* 
*/

public class App {

    public App(String targetFilePath) {
        String filePath = targetFilePath;
	try(Workbook workbook = readWorkbook(filePath)) {
	    boolean result = calculate(workbook);            
            boolean exitRequested = false;
	
	    // Application main loop
            while (!exitRequested) {
                // HashMap<> changes = processInput(askForInput());
            }
	} catch (IOException e) {
		e.printStackTrace();
	}

    }

    private Workbook readWorkbook(String path) throws IOException {
        InputStream fis = new FileInputStream(path);
		Workbook wb;
		if (path.endsWith(".xlsx")) {
				wb = WorkbookFactory.create(fis);
		} else return null;
		return wb;
	}

	private boolean calculate(Workbook wb) {
		return false;
	}

	private HashMap processInput(String input) {
		return null;
	}

	private String askForInput() {
		return "Not implemented yet";
	}




    public static void main( String[] args )
    {
        if (args.length < 1) {
	    System.out.println("Not enough arguments!");
	    System.exit(-1);
	}
	App app = new App(args[0]);
	System.exit(0);
    }

    
}
