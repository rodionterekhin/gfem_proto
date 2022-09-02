package ru.gazpromneft.gfemproto;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    
    String filePath;
    Workbook workbook;

    public App(String targetFilePath) {
		this.filePath = targetFilePath;
		this.workbook = readWorkbook(filePath);
		boolean result = calculate(workbook);
        boolean exitRequested = false;
	
		// Application main loop
		while (!exitRequested) {
	    	// HashMap<> changes = processInput(askForInput());
		}

    }

    private Workbook readWorkbook(String path) throws IOException {
        InputStream fis = new FileInputStream(path);
		Workbook wb;
		if (path.endsWith(".xlsx")) {
				wb = WorkbookFactory.create(fis);
		}
		return wb;
	}

	private calculate(Workbook wb) {
		;
	}

	private HashMap<> processInput(String input) {
		return null;
	}

	private askForInput() {
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
