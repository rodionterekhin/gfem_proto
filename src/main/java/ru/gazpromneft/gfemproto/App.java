package ru.gazpromneft.gfemproto;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creation-Time: 14:57
 * Creation-Date: 02/09/2022
 * Author: Terekhin Rodion
 * Author-Email: rodionterekhin@gmail.com
 */

public class App implements IMainController {

    private final MainFrame mf;
    private Workbook model;
    private final Logger logger;

    public App() {
        MainFrame.updateLookAndFeel();
        tryRegisterSLN();
        mf = new MainFrame(this);

        logger = Logger.getLogger(this.getClass().getName());
    }


    private void tryRegisterSLN() {
        try {
            WorkbookEvaluator.registerFunction("SLN", new SLN());
        } catch (java.lang.IllegalArgumentException ex) {
            System.out.println("SLN already registered");
        }
    }

    private Workbook readWorkbook(File file) {
        try {
            String path = file.getAbsolutePath();
            InputStream fis = new FileInputStream(path);
            Workbook wb;
            if (path.endsWith(".xlsx") || path.endsWith(".xls") || path.endsWith(".xlsb")) {
                wb = WorkbookFactory.create(fis);
            } else return null;
            return wb;
        } catch (IOException exception) {
            logger.log(Level.SEVERE, "Cannot open file!");
            return null;
        } catch (EncryptedDocumentException exception) {
            logger.log(Level.SEVERE, "The file is encrypted!");
            return null;
        }
    }

    private boolean calculate(Workbook wb, boolean clearCache) {
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        StringBuilder report = new StringBuilder();

        if (clearCache)
            evaluator.clearAllCachedResultValues();

        long load_time = System.nanoTime();
        evaluator.evaluateAll();
        // TODO CustomEvaluator: check if it is possible to calculate formulas
        //  without need to them have all the arguments already calculated

        System.out.printf("Workbook evaluation time is: %f mS%n", (double) (System.nanoTime() - load_time) / 1e6);
        Sheet output = wb.getSheet("output");

        for (Row r : output) {
            if (r.getRowNum() == 0)
                continue;
            String outputType = r.getCell(0).getStringCellValue();
            String outputName = r.getCell(1).getStringCellValue();
            double outputValue = r.getCell(2).getNumericCellValue();
            report.append(outputType)
                    .append(": ")
                    .append(outputName)
                    .append(": ")
                    .append(outputValue)
                    .append("\n");
        }
        mf.showError(report.toString());
        return true;
    }


    public static void main() {
        new App();
    }


    @Override
    public String loadModel() {
        File modelFile = mf.openFileDialog();
        if (Objects.isNull(modelFile)) return ""; // Nothing was chosen
        Workbook probablyModel = readWorkbook(modelFile);
        if (Objects.isNull(probablyModel)) {
            mf.showError("Файл невозможно прочитать!");
            return "";
        }
        if (!validateModel(probablyModel)) {
            mf.showError("Модель не соответствует формату!");
            return "";
        }
        model = probablyModel;

        return modelFile.getAbsolutePath();
    }

    private boolean validateModel(Workbook modelWorkbook) {
        boolean correct = true;

        // check sheets available
        correct &= (modelWorkbook.getSheet("input") != null);
        correct &= (modelWorkbook.getSheet("output") != null);

        if (!correct) return false;

        Sheet input = modelWorkbook.getSheet("input");
        Sheet output = modelWorkbook.getSheet("output");

        Map<String, String> map = new HashMap<>();

        map.put("A1", "Тип");
        map.put("B1", "Имя");
        map.put("C1", "Значения");

        //check input sheet format
        for (Map.Entry<String, String> e : map.entrySet()) {
            CellAddress addr = new CellAddress(e.getKey());
            int row = addr.getRow();
            int col = addr.getColumn();
            Cell cell = input.getRow(row).getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            correct &= (Objects.equals(cell.getRichStringCellValue().getString(), e.getValue()));
        }

        map.put("C1", "Ссылки");

        //check output sheet format
        for (Map.Entry<String, String> e : map.entrySet()) {
            CellAddress addr = new CellAddress(e.getKey());
            int row = addr.getRow();
            int col = addr.getColumn();
            Cell cell = output.getRow(row).getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            correct &= (Objects.equals(cell.getRichStringCellValue().getString(), e.getValue()));
        }


        return correct;
    }


    @Override
    public String loadData() {
        File dataFile = mf.openFileDialog();
        if (dataFile != null) {
            return dataFile.getAbsolutePath();
        } else return "";
    }

    @Override
    public void calculate() {
        if (Objects.isNull(model)) {
            mf.showError("Сначала подгрузите модель!");
            return;
        }
        calculate(model, true);
    }
}
