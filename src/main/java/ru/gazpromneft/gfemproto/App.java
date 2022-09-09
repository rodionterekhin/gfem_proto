package ru.gazpromneft.gfemproto;

import com.formdev.flatlaf.FlatIntelliJLaf;
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import ru.gazpromneft.gfemproto.gui.GfemGUI;
import ru.gazpromneft.gfemproto.gui.IMainController;
import ru.gazpromneft.gfemproto.model.*;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creation-Time: 14:57
 * Creation-Date: 02/09/2022
 * Author: Terekhin Rodion
 * Author-Email: rodionterekhin@gmail.com
 */

public class App implements IMainController {

    private final Object EMPTY_MODEL = Conventions.EMPTY_MODEL;

    private final GfemGUI mf;
    private final Logger logger;
    private DefaultMutableTreeNode selectedNode;
    private boolean comboBoxLock;

    public App() {
        FlatIntelliJLaf.setup();
        // GfemGUI.updateLookAndFeel();
        logger = Logger.getLogger(this.getClass().getName());
        tryRegisterSLN();
        mf = new GfemGUI(this);
        mf.setTreeModel(new ExcelTreeModel());
        clearComboBox();
        if (loadStateExists()) {
            try {
                mf.setTreeModel(loadState());
            } catch (IOException e) {
                mf.showError("Файл состояния повержден и не может быть восстановлен!");
                logger.log(Level.SEVERE, e.getMessage(), e);
                mf.setTreeModel(new ExcelTreeModel());
            } catch (ClassNotFoundException e) {
                mf.showError("Файл состояния предназначен для другой версии программы!");
                logger.log(Level.SEVERE, e.getMessage(), e);
                mf.setTreeModel(new ExcelTreeModel());
            }
        }
        mf.setVisible(true);
    }


    private void tryRegisterSLN() {
        try {
            WorkbookEvaluator.registerFunction("SLN", new SLN());
        } catch (java.lang.IllegalArgumentException ex) {
            logger.log(Level.INFO, "SLN already registered");
        }
    }

    public static void main(String[] args) {
        WorkbookFactory.addProvider(new HSSFWorkbookFactory());
        WorkbookFactory.addProvider(new XSSFWorkbookFactory());
        new App();
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
            showAllModelsInComboBox();
            mf.setStatus("Загружена модель " + model);
            return modelFile.getAbsolutePath();
        } catch (ModelLoadException | ModelValidationException e) {
            logger.warning(e.getMessage());
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
            mf.setStatus("Загружен кейс " + data);
            return dataFile.getAbsolutePath();
        } catch (InputDataLoadException e) {
            logger.warning(e.getMessage());
            mf.showError(e.getMessage());
            return "";
        }
    }

    private void showAllModelsInComboBox() {
        comboBoxLock = true;
        Object selected = mf.getComboboxSelected();
        List<Object> values = ((ExcelTreeModel) mf.getTreeModel()).getModels();
        values.add(EMPTY_MODEL);
        mf.setComboboxValues(values.toArray());
        setComboboxSelected(selected);
        comboBoxLock = false;
    }


    private void clearComboBox() {
        comboBoxLock = true;
        mf.setComboboxValues();
        comboBoxLock = false;
    }

    private void setComboboxSelected(Object value) {
        comboBoxLock = true;
        mf.setComboboxSelected(value);
        comboBoxLock = false;
    }

    @Override
    public void calculate() {
        if (selectedNode != null) {
            if (!(selectedNode.getUserObject() instanceof InputData dataToCalculate))
                return;
            logger.info("Calculation requested for data \"" + selectedNode + "\"");
            CalculationSchema calculationSchema = new CalculationSchema(dataToCalculate);
            Supplier<CalculationSchema> calculationSupplier = () -> {
                logger.info("Scheduled calculation for schema " + calculationSchema);
                mf.setStatus("Выполняю расчет " + calculationSchema);
                return Calculator.calculate(calculationSchema);
            };
            CompletableFuture.supplyAsync(calculationSupplier)
                    .exceptionally(this::onCalculationError)
                    .thenAccept(this::onCalculated);
        }
    }

    private CalculationSchema onCalculationError(Throwable throwable) {
        CalculationError error = (CalculationError) throwable.getCause();
        logger.info("Error occured during calculation of schema " + error.getSchema() + "");
        mf.setStatus("При расчете схемы " + error.getSchema() + " произошла ошибка");
        logger.log(Level.SEVERE, error.getMessage(), error.getTrueReason());
        return error.getSchema();
    }

    private void onCalculated(CalculationSchema schema) {
        if (!schema.isCompleted()) {
            logger.info("Schema " + schema + " not completed");
            return;
        }
        logger.info("Calculation done for schema " + schema + "");
        mf.setStatus("Расчет " + schema + " выполнен");
        // TODO result display in GUI
        mf.showInfo(schema.getResult().getTextReport());
        logger.info(schema.getResult().asMap().toString());
    }

    public void exit() {
        try {
            saveState();
        } catch (Exception e) {
            mf.showError("Невозможно сохранить модели и данные в файл!");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        System.exit(0);
    }

    @Override
    public void about() {
        mf.showInfo(Conventions.ABOUT_MESSAGE);
    }

    @Override
    public void treeSelectionChanged(Object selectedObject) {

        mf.clearInputEntries();
        mf.clearOutputEntries();
        if (Objects.isNull(selectedObject)) {
            clearComboBox();
            selectedNode = null;
            return;
        }
        selectedNode = (DefaultMutableTreeNode) selectedObject;
        Object uncastObject = selectedNode.getUserObject();
        if (uncastObject instanceof InputData selected) {
            showAllModelsInComboBox();
            for(Entry<String, Object> e: selected.asMap().entrySet()) {
                if (e.getValue() instanceof Number)
                    mf.addInputNumericEntry(e.getKey(), String.valueOf(e.getValue()));
                else if (e.getValue() instanceof HashMap<?, ?> hashMap)
                    mf.addInputArrayEntry(e.getKey(), (HashMap<Double, Double>) hashMap);
            }
            Object shouldSelect = selected.getAttachedModel();
            if (Objects.isNull(shouldSelect))
                shouldSelect = EMPTY_MODEL;
            setComboboxSelected(shouldSelect);
        } else {
            clearComboBox();
        }

    }

    @Override
    public void changedModel() {
        if (comboBoxLock)
            return;
        logger.log(Level.INFO, "Combobox action performed");
        if (selectedNode != null) {
            Object uncastObject = selectedNode.getUserObject();
            if (uncastObject instanceof InputData selected) {
                if (Objects.isNull(mf.getComboboxSelected()))
                    return;
                if (mf.getComboboxSelected().equals(EMPTY_MODEL)) {
                    selected.attachModel(null);
                } else {
                    selected.attachModel((ExcelModel) mf.getComboboxSelected());
                }
            }
        }
    }

    @Override
    public void available_functions() {
        final int columnsCount = 5;
        StringBuilder msg = new StringBuilder("Список поддерживаемых функций:\n");
        AtomicInteger i = new AtomicInteger(1);
        FunctionEval.getSupportedFunctionNames().forEach((a) -> msg.append(a).append((i.getAndIncrement()%columnsCount)==0 ?"\n" : ", "));
        mf.showInfo(msg.substring(0, msg.length() - 2));
    }

    @Override
    public void deleteNode() {
        if (Objects.isNull(selectedNode))
            return;
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
        logger.info("Started saving state");
        FileOutputStream outputStream = new FileOutputStream(Conventions.STATE_FILE_NAME);
        logger.info("Created file output stream");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        logger.info("Created object output stream");
        // сохраняем дерево в файл
        objectOutputStream.writeObject(mf.getTreeModel());
        logger.info("Saved tree model to output stream");

        //закрываем поток и освобождаем ресурсы
        objectOutputStream.close();
        logger.info("Closed output stream");
    }

    private boolean loadStateExists() {
        File f = new File(Conventions.STATE_FILE_NAME);
        return f.exists();
    }
    private ExcelTreeModel loadState() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(Conventions.STATE_FILE_NAME);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        ExcelTreeModel savedTreeModel = (ExcelTreeModel) objectInputStream.readObject();
        objectInputStream.close();

        return savedTreeModel;
    }
}
