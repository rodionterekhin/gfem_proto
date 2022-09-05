package ru.gazpromneft.gfemproto;

import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import ru.gazpromneft.gfemproto.model.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.io.File;

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

    private final GfemGUI mf;
    private ExcelModel model;
    private InputData data;
    private final Logger logger;
    protected FormulaEvaluator formulaEvaluator;

    public App() {
        GfemGUI.updateLookAndFeel();
        logger = Logger.getLogger(this.getClass().getName());
        tryRegisterSLN();
        mf = new GfemGUI(this);
        TreeNode rootNode = new DefaultMutableTreeNode("Данные проекта");
        mf.setTreeModel(new DefaultTreeModel(rootNode));

    }


    private void tryRegisterSLN() {
        try {
            WorkbookEvaluator.registerFunction("SLN", new SLN());
        } catch (java.lang.IllegalArgumentException ex) {
            logger.log(Level.INFO, "SLN already registered");
        }
    }

    private boolean calculate(Workbook wb, boolean clearCache) {

        formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
        StringBuilder report = new StringBuilder();

        if (clearCache)
            formulaEvaluator.clearAllCachedResultValues();

        long load_time = System.nanoTime();
        formulaEvaluator.evaluateAll();

        System.out.printf("Workbook evaluation time is: %f mS%n", (double) (System.nanoTime() - load_time) / 1e6);
        Sheet model = wb.getSheet("model");

        for (Row r : model) {
            for (Cell c : r) {
                try {
                    if (c.getCellType() == CellType.FORMULA) {
                        double a = c.getNumericCellValue();
                    }
                }
                catch (Exception e) {
                    System.out.println(c.getAddress());
                }
            }
        }


        Sheet output = wb.getSheet("output");

        for (Row r : output) {
            if (r.getRowNum() == 0)
                continue;
            String outputType = r.getCell(0).getStringCellValue();
            String outputName = r.getCell(1).getStringCellValue();
            double outputValue = r.getCell(2).getNumericCellValue();
            report.append(outputName)
                    .append(" = ")
                    .append(outputValue)
                    .append("\n");
        }
        mf.showInfo(report.toString());
        return true;
    }


    public static void main(String[] args) {
        WorkbookFactory.addProvider(new HSSFWorkbookFactory());
        WorkbookFactory.addProvider(new XSSFWorkbookFactory());
        for (String a : FunctionEval.getSupportedFunctionNames())
            System.out.println(a);
        new App();
    }


    private void updateModelWithData(Workbook model, Workbook data) {
        Sheet dataSheet = data.getSheetAt(0);
        Sheet modelDataSheet = model.getSheet("input");


        for (Row r : dataSheet) {
            for (Cell sourceCell : r) {
                Cell targetCell = modelDataSheet.getRow(r.getRowNum())
                        .getCell(sourceCell.getColumnIndex());
                switch (sourceCell.getCellType()) {
                    case FORMULA -> targetCell.setCellFormula(sourceCell.getCellFormula());
                    case STRING -> targetCell.setCellValue(sourceCell.getStringCellValue());
                    case NUMERIC -> targetCell.setCellValue(sourceCell.getNumericCellValue());
                    case BLANK -> targetCell.setBlank();
                }
            }
        }
    }

    @Override
    public String loadModel() {
        File modelFile = mf.openFileDialog();
        if (Objects.isNull(modelFile))
            return "";
        try {
            this.model = ExcelModelFactory.fromFile(modelFile);
        } catch (ModelCreationException | ModelValidationException e) {
            mf.showError(e.getMessage());
            return "";
        }

        return modelFile.getAbsolutePath();
    }


    @Override
    public String loadData() {
        File dataFile = mf.openFileDialog();
        if (Objects.isNull(dataFile))
            return "";
        try {
            this.data = InputDataFactory.fromFile(dataFile);
        } catch (InputDataCreationException e) {
            mf.showError(e.getMessage());
            return "";
        }
        return dataFile.getAbsolutePath();
    }

    @Override
    public void calculate() {
        if (Objects.isNull(model)) {
            mf.showError("Сначала подгрузите модель!");
            return;
        }
        if (data != null)
            model.setData(data);
        model.calculate();
    }

    public void exit() {
        if (model != null) {
            model.release();
        }
        if (data != null) {
            data.release();
        }
        System.exit(0);
    }

    @Override
    public void about() {
        mf.showInfo("Apache POI Demo\nWritten by Terekhin Rodion");
    }
}
