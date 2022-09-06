package ru.gazpromneft.gfemproto;

import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import ru.gazpromneft.gfemproto.model.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.io.*;

import java.util.Map.Entry;
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
    private DefaultMutableTreeNode selectedNode;

    public App() {
        GfemGUI.updateLookAndFeel();
        logger = Logger.getLogger(this.getClass().getName());
        tryRegisterSLN();
        mf = new GfemGUI(this);
        mf.setTreeModel(new ExcelTreeModel());
        try {
            mf.setTreeModel(loadState());
        } catch (IOException e) {
            mf.showError("Нет файла с предыдущим состоянием модели!");
            mf.setTreeModel(new ExcelTreeModel());
        } catch (ClassNotFoundException e) {
            mf.showError("Файл состояния поврежден!");
            mf.setTreeModel(new ExcelTreeModel());
        }
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
            ExcelModel model = ExcelModelFactory.fromFile(modelFile);
            if (((ExcelTreeModel)mf.getTreeModel()).modelsContain(model))
                throw new ModelLoadException("Выбранная модель уже загружена!");
            ((ExcelTreeModel)mf.getTreeModel()).addModelNode(model);
            return modelFile.getAbsolutePath();
        } catch (ModelLoadException | ModelValidationException e) {
            mf.showError(e.getMessage());
            return "";
        }
    }


    @Override
    public String loadCase() {
        File dataFile = mf.openFileDialog();
        if (Objects.isNull(dataFile))
            return "";
        try {
            InputData data = InputDataFactory.fromFile(dataFile);
            if (((ExcelTreeModel)mf.getTreeModel()).casesContain(data))
                throw new InputDataLoadException("Данные уже были загружены!");
            ((ExcelTreeModel)mf.getTreeModel()).addCaseNode(data);
            return dataFile.getAbsolutePath();
        } catch (InputDataLoadException e) {
            mf.showError(e.getMessage());
            return "";
        }
    }

    @Override
    public void calculate() {

    }

    public void exit() {
        try {
            saveState();
        } catch (Exception e) {
            mf.showError("Невозможно сохранить модели и данные в файл!");
        }
        System.exit(0);
    }

    @Override
    public void about() {
        mf.showInfo("Apache POI Demo\nWritten by Terekhin Rodion");
    }

    @Override
    public void treeSelectionChanged(Object selectedObject) {

        mf.clearInputEntries();
        mf.clearOutputEntries();
        if (Objects.isNull(selectedObject)) {
            selectedNode = null;
            return;
        }
        selectedNode = (DefaultMutableTreeNode) selectedObject;
        Object uncastObject = selectedNode.getUserObject();
        if (uncastObject instanceof InputData selected) {
            for(Entry<String, Object> e: selected.asMap().entrySet()) {
                mf.addInputNumericEntry(e.getKey(), String.valueOf(e.getValue()));
            }
        }
    }

    @Override
    public void deleteNode() {
        Object data = selectedNode.getUserObject();
        if (data instanceof InputData || data instanceof ExcelModel) {
            ((ExcelTreeModel) mf.getTreeModel()).deleteNode(selectedNode);
        } else {
            logger.log(Level.INFO, "Trying to delete a non-user node, ha-ha");
            // Just chilling
        }
    }

    @Override
    public void duplicateNode() {
        ExcelTreeModel etm =  (ExcelTreeModel) mf.getTreeModel();
        if (etm.modelsContain(selectedNode)) {
            DefaultMutableTreeNode duplicate = (DefaultMutableTreeNode) selectedNode.clone();
            ExcelModel model = (ExcelModel) duplicate.getUserObject();
            model.changeName(model + " (copy)");
            etm.addModelNode(model);
        } else if (etm.casesContain(selectedNode)) {
            DefaultMutableTreeNode duplicate = (DefaultMutableTreeNode) selectedNode.clone();
            InputData data = (InputData) duplicate.getUserObject();
            data.changeName(data + " (copy)");
            etm.addCaseNode(data);
        } else {
            assert false;
        }
    }

    private void saveState() throws IOException {
        FileOutputStream outputStream = new FileOutputStream(Conventions.STATE_FILE_NAME);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        // сохраняем дерево в файл
        objectOutputStream.writeObject(mf.getTreeModel());

        //закрываем поток и освобождаем ресурсы
        objectOutputStream.close();
    }

    private ExcelTreeModel loadState() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(Conventions.STATE_FILE_NAME);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        ExcelTreeModel savedTreeModel = (ExcelTreeModel) objectInputStream.readObject();
        objectInputStream.close();

        return savedTreeModel;
    }
}
