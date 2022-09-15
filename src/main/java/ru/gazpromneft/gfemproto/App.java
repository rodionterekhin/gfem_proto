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

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.*;

import java.net.URL;
import java.nio.channels.OverlappingFileLockException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
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

    private static final int LOCK_WAITING_PERIOD = 1;       // seconds
    private static final int TOTAL_LOCK_WAITING_TIME = 15;  // seconds
    private static final int TOTAL_LOCK_READING_WAITING_TIME = 5;  // seconds
    private final Object EMPTY_MODEL = Conventions.EMPTY_MODEL;

    private final GfemGUI gui;
    private final Logger logger;
    private DefaultMutableTreeNode selectedNode;
    private boolean comboBoxLock;
    private List<List<Object>> tableData;
    private List<Number> tableIndex;
    private boolean canUseStateFile = true;

    public App() {
        ExcelTreeSaver.configure(LOCK_WAITING_PERIOD,
                TOTAL_LOCK_WAITING_TIME,
                TOTAL_LOCK_READING_WAITING_TIME);
        logger = Logger.getLogger(this.getClass().getName());
        FlatIntelliJLaf.setup();
        Image icon = getAppIcon();
        tryRegisterSLN();
        gui = new GfemGUI(this, icon);
        //
        tryLoadState();
        SwingUtilities.invokeLater(this::refresh);
        gui.setVisible(true);
        gui.setStatus("Готово");
    }

    private void tryLoadState() {
        if (loadStateExists()) {
            try {
                gui.setTreeModel(loadState());
            } catch (OverlappingFileLockException e) {
                gui.showError("Файл состояния используется другим процессом!");
                gui.setStatusPrefix("Изменения не будут сохранены");
                logger.log(Level.SEVERE, e.getMessage(), e);
                canUseStateFile = false;
                gui.setTreeModel(new ExcelTreeModel());
            } catch (IOException e) {
                gui.showError("Файл состояния повержден и не может быть восстановлен!");
                logger.log(Level.SEVERE, e.getMessage(), e);
                gui.setTreeModel(new ExcelTreeModel());
            } catch (ClassNotFoundException e) {
                gui.showError("Файл состояния предназначен для другой версии программы!");
                logger.log(Level.SEVERE, e.getMessage(), e);
                gui.setTreeModel(new ExcelTreeModel());
            }
        } else {
            logger.info("No state file found. Assuming this is the first time the application is opened");
            gui.setTreeModel(new ExcelTreeModel());
        }
    }

    private Image getAppIcon() {
        Image icon = null;
        URL imgURL = getClass().getResource(Conventions.ICON_PATH);
        if (imgURL != null) {
            icon = new ImageIcon(imgURL).getImage();
        } else {
            logger.severe("Couldn't find file: " + Conventions.ICON_PATH);
        }
        return icon;
    }


    private void tryRegisterSLN() {
        try {
            WorkbookEvaluator.registerFunction("SLN", new SLN());
        } catch (java.lang.IllegalArgumentException ex) {
            logger.log(Level.INFO, "SLN already registered");
        }
    }

    public static void main(String[] args) {
        createSplashScreen();
        WorkbookFactory.addProvider(new HSSFWorkbookFactory());
        WorkbookFactory.addProvider(new XSSFWorkbookFactory());
        new App();
    }

    private static void createSplashScreen() {
        final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            Logger.getAnonymousLogger().warning("SplashScreen.getSplashScreen() returned null");
            return;
        }
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            Logger.getAnonymousLogger().warning("g is null");
        }
    }


    @Override
    public String loadModel() {
        File modelFile = gui.openFileDialog();
        if (Objects.isNull(modelFile))
            return "";
        try {
            ExcelModel model = ExcelModelFactory.fromFile(modelFile);
            if (((ExcelTreeModel) gui.getTreeModel()).modelsContain(model))
                throw new ModelLoadException("Выбранная модель уже загружена!");
            ((ExcelTreeModel) gui.getTreeModel()).addModelNode(model);
            showAllModelsInComboBox();
            gui.setStatus("Загружена модель " + model);
            return modelFile.getAbsolutePath();
        } catch (ModelLoadException | ModelValidationException e) {
            logger.warning(e.getMessage());
            gui.showError(e.getMessage());
            return "";
        }
    }

    public void copyFrom(CalculationSchema schema) {
        ((ExcelTreeModel) gui.getTreeModel()).addCaseNode(new CalculationSchema(schema.getModel(), schema.getInputData()));
        gui.setStatus("Дублирован кейс " + schema.getInputData());
    }

    @Override
    public String loadCase() {
        File dataFile = gui.openFileDialog();
        if (Objects.isNull(dataFile))
            return "";
        try {
            InputData data = InputDataFactory.fromFile(dataFile);
            CalculationSchema schema = new CalculationSchema(null, data);
            ((ExcelTreeModel) gui.getTreeModel()).addCaseNode(schema);
            gui.setStatus("Загружен кейс " + data);
            return dataFile.getAbsolutePath();
        } catch (InputDataLoadException e) {
            logger.warning(e.getMessage());
            gui.showError(e.getMessage());
            return "";
        }
    }

    private void showAllModelsInComboBox() {
        comboBoxLock = true;
        Object selected = gui.getComboboxSelected();
        List<Object> values = ((ExcelTreeModel) gui.getTreeModel()).getModels();
        values.add(EMPTY_MODEL);
        gui.setComboboxValues(values.toArray());
        setComboboxSelected(selected);
        comboBoxLock = false;
    }


    private void clearComboBox() {
        comboBoxLock = true;
        gui.setComboboxValues();
        comboBoxLock = false;
    }

    private void setComboboxSelected(Object value) {
        comboBoxLock = true;
        gui.setComboboxSelected(value);
        comboBoxLock = false;
    }

    @Override
    public void calculate() {
        if (selectedNode != null) {
            if (!(selectedNode.getUserObject() instanceof CalculationSchema))
                return;
            CalculationSchema calculationSchema = (CalculationSchema) selectedNode.getUserObject();
            if (Objects.isNull(calculationSchema.getModel()))
                return;
            logger.info("Calculation requested for data \"" + selectedNode + "\"");
            Supplier<CalculationSchema> calculationSupplier = () -> {
                logger.info("Scheduled calculation for schema " + calculationSchema);
                gui.setStatus("Выполняю расчет " + calculationSchema);
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
        gui.setStatus("При расчете схемы " + error.getSchema() + " произошла ошибка");
        logger.log(Level.SEVERE, error.getMessage(), error.getTrueReason());
        return error.getSchema();
    }

    private void onCalculated(CalculationSchema schema) {
        if (!schema.isCompleted()) {
            logger.info("Schema " + schema + " not completed");
            return;
        }
        logger.info("Calculation done for schema " + schema + "");
        NumberFormat formatter = new DecimalFormat("#0");
        gui.setStatus("Расчет " + schema + " выполнен (" + formatter.format(schema.getCalculationTime()) + " мс)");
        refresh();
        logger.info(schema.getResult().asMap().toString());
        // mf.showInfo("Расчет " + schema + " выполнен");
        gui.focusOnOutput();
    }

    /**
     * Function that reads current programme state and updates all view in accordance to that state.
     * Dependent elements are:
     * 1) Combo box
     * 2) Tree view
     */
    private void refresh() {
        comboBoxLock = true;
        gui.clearInputEntries();
        gui.clearOutputEntries();
        gui.setBtnToExcelState(false);
        gui.addInputTable(null);
        if (Objects.isNull(selectedNode)) {
            clearComboBox();
            return;
        }
        Object uncastObject = selectedNode.getUserObject();
        if (uncastObject instanceof CalculationSchema) {
            CalculationSchema selectedSchema = (CalculationSchema) uncastObject;
            showAllModelsInComboBox();
            Object shouldSelect = selectedSchema.getModel();
            if (Objects.isNull(shouldSelect))
                shouldSelect = EMPTY_MODEL;
            setComboboxSelected(shouldSelect);

            tableData = new ArrayList<>();
            for (Entry<String, Object> e : selectedSchema.getInputData().asMap().entrySet()) {
                if (e.getValue() instanceof Number)
                    gui.addInputNumericEntry(e.getKey(), (Number) e.getValue());
                else if (e.getValue() instanceof HashMap<?, ?>) {
                    addToTable(e.getKey(), (HashMap<Number, Number>) e.getValue());
                    // mf.addInputArrayEntry(e.getKey(), (HashMap<Number, Number>) hashMap);
                }
            }
            gui.addInputTable(getTableModel());

            if (!selectedSchema.isCompleted()) {
                gui.addOutputNumericEntry("Требуется расчет", null);
            } else {
                gui.setBtnToExcelState(true);
                for (Entry<String, Object> e : selectedSchema.getResult().asMap().entrySet()) {
                    if (e.getValue() instanceof Number) {
                        gui.addOutputNumericEntry(e.getKey(), (Number) e.getValue());
                    }
                }
            }
        } else {
            clearComboBox();
        }
        gui.updateAll();
        comboBoxLock = false;
    }

    private TableModel getTableModel() {
        return new ExcelTableModel(tableIndex, tableData);
    }

    private void addToTable(String key, HashMap<Number, Number> value) {
        Set<Number> indexSet = new HashSet<>();
        value.forEach((k, v) -> indexSet.add(k));
        tableIndex = new ArrayList<>();
        List<Object> tableRow = new ArrayList<>(Collections.singleton(key));
        indexSet.stream().sorted().forEach((k) ->
        {
            tableRow.add(value.get(k));
            tableIndex.add(k);
        });
        tableData.add(tableRow);
    }

    public void exit() {
        if (canUseStateFile) {
            try {
                saveState();
            } catch (Exception e) {
                gui.showError("Невозможно сохранить модели и данные в файл!");
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        System.exit(0);
    }

    @Override
    public void about() {
        gui.showInfo(Conventions.ABOUT_MESSAGE);
    }

    @Override
    public void treeSelectionChanged(Object selectedObject) {
        logger.info("Tree selection changed");
        if (Objects.isNull(selectedObject)) {
            selectedNode = null;
            return;
        }
        selectedNode = (DefaultMutableTreeNode) selectedObject;
        refresh();
        logger.info("Refreshed");
    }

    @Override
    public void changedModel() {
        if (comboBoxLock)
            return;
        logger.log(Level.INFO, "Combobox action performed");
        if (selectedNode != null) {
            Object uncastObject = selectedNode.getUserObject();
            if (uncastObject instanceof CalculationSchema) {
                CalculationSchema selected = (CalculationSchema) uncastObject;
                if (Objects.isNull(gui.getComboboxSelected()))
                    gui.setComboboxSelected(EMPTY_MODEL);
                if (gui.getComboboxSelected().equals(EMPTY_MODEL)) {
                    selected.setModel(null);
                } else {
                    selected.setModel((ExcelModel) gui.getComboboxSelected());
                }
            }
        }
        refresh();
    }

    @Override
    public void available_functions() {
        final int columnsCount = 5;
        StringBuilder msg = new StringBuilder("Список поддерживаемых функций:\n");
        AtomicInteger i = new AtomicInteger(1);
        FunctionEval.getSupportedFunctionNames().forEach((a) -> msg.append(a).append((i.getAndIncrement() % columnsCount) == 0 ? "\n" : ", "));
        gui.showInfo(msg.substring(0, msg.length() - 2));
    }

    @Override
    public void saveToExcel() {
        if (Objects.isNull(selectedNode))
            return;
        Object data = selectedNode.getUserObject();
        if (data instanceof CalculationSchema) {
            CalculationSchema selectedSchema = (CalculationSchema) data;
            File destination = gui.saveFileDialog();
            if (Objects.isNull(destination))
                return;
            if (!destination.getAbsolutePath().endsWith(".xlsx"))
                destination = new File(destination.getAbsolutePath() + ".xlsx");
            try {
                selectedSchema.saveToFile(destination);
            } catch (IOException e) {
                logger.warning(e.getMessage());
                gui.showError(e.getMessage());
            }
        }
    }

    @Override
    public void deleteNode() {
        if (Objects.isNull(selectedNode))
            return;
        Object data = selectedNode.getUserObject();
        if (data instanceof CalculationSchema || data instanceof ExcelModel) {
            ((ExcelTreeModel) gui.getTreeModel()).deleteNode(selectedNode);
        } else {
            logger.log(Level.INFO, "Trying to delete a non-user node");
            // Just chilling
        }
    }

    @Override
    public void duplicateNode() {
        if (selectedNode != null && selectedNode.getUserObject() instanceof CalculationSchema) {
            copyFrom((CalculationSchema) selectedNode.getUserObject());
        }
    }

    private void saveState() throws IOException {
        File file = new File(Conventions.STATE_FILE_NAME);
        ExcelTreeSaver.toFile((ExcelTreeModel) gui.getTreeModel(), file);
    }

    private boolean loadStateExists() {
        File f = new File(Conventions.STATE_FILE_NAME);
        return f.exists();
    }

    private ExcelTreeModel loadState() throws IOException, ClassNotFoundException {
        File file = new File(Conventions.STATE_FILE_NAME);
        return ExcelTreeSaver.fromFile(file);
    }


}
