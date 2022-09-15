package ru.gazpromneft.gfemproto;

import org.apache.poi.ss.formula.eval.FunctionEval;
import ru.gazpromneft.gfemproto.gui.GfemGUI;
import ru.gazpromneft.gfemproto.gui.IMainController;
import ru.gazpromneft.gfemproto.model.*;

import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements IMainController {

    private DefaultMutableTreeNode selectedNode;
    private boolean comboBoxLock;
    private List<List<Object>> tableData;
    private List<Number> tableIndex;
    private GfemGUI gui;
    private final ResourceBundle strings;
    private final Logger logger;

    public MainController() {
        logger = Logger.getLogger(this.getClass().getName());
        strings = App.getStrings();
    }

    @Override
    public String loadModel() {
        File modelFile = gui.openFileDialog(strings.getString("dialog.open.model"));
        if (Objects.isNull(modelFile)) return "";
        try {
            ExcelModel model = ExcelModelFactory.fromFile(modelFile);
            if (((ExcelTreeModel) gui.getTreeModel()).modelsContain(model))
                throw new ModelLoadException(strings.getString("add.model.error.duplicate"));
            ((ExcelTreeModel) gui.getTreeModel()).addModelNode(model);
            showAllModelsInComboBox();
            gui.setStatus(strings.getString("status.add.model") + " " + model);
            return modelFile.getAbsolutePath();
        } catch (ModelLoadException | ModelValidationException e) {
            logger.warning(e.getMessage());
            gui.showError(e.getMessage());
            return "";
        }
    }

    public void copyFrom(CalculationSchema schema) {
        ((ExcelTreeModel) gui.getTreeModel()).addSchemaNode(new CalculationSchema(schema.getModel(), schema.getInputData()));
        gui.setStatus(strings.getString("status.duplicate.element") + " " + schema.getInputData());
    }

    @Override
    public String loadCase() {
        File dataFile = gui.openFileDialog(strings.getString("dialog.open.data"));
        if (Objects.isNull(dataFile)) return "";
        try {
            InputData data = InputDataFactory.fromFile(dataFile);
            CalculationSchema schema = new CalculationSchema(null, data);
            ((ExcelTreeModel) gui.getTreeModel()).addSchemaNode(schema);
            gui.setStatus(strings.getString("status.add.data") + " " + data);
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
        values.add(App.getInstance().EMPTY_MODEL);
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
            if (!(selectedNode.getUserObject() instanceof CalculationSchema)) return;
            CalculationSchema calculationSchema = (CalculationSchema) selectedNode.getUserObject();
            if (Objects.isNull(calculationSchema.getModel())) return;
            logger.info("Calculation requested for data \"" + selectedNode + "\"");
            Supplier<CalculationSchema> calculationSupplier = () -> {
                logger.info("Scheduled calculation for schema " + calculationSchema);
                gui.setStatus(strings.getString("status.calculating") + " " + calculationSchema);
                return Calculator.calculate(calculationSchema);
            };
            CompletableFuture.supplyAsync(calculationSupplier).exceptionally(this::onCalculationError).thenAccept(this::onCalculated);
        }
    }

    @Override
    public void exit() {
        App.getInstance().exit();
    }

    @Override
    public void about() {
        String buildVersion = App.getInstance().BUILD_VERSION;
        String buildTime = App.getInstance().BUILD_TIME;
        String mainString = strings.getString("application.info");
        String text = String.format(mainString, buildVersion, buildTime);
        gui.showInfo(text);
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
        if (comboBoxLock) return;
        logger.log(Level.INFO, "Combobox action performed");
        if (selectedNode != null) {
            Object uncastObject = selectedNode.getUserObject();
            if (uncastObject instanceof CalculationSchema) {
                CalculationSchema selected = (CalculationSchema) uncastObject;
                if (Objects.isNull(gui.getComboboxSelected())) gui.setComboboxSelected(App.getInstance().EMPTY_MODEL);
                if (gui.getComboboxSelected().equals(App.getInstance().EMPTY_MODEL)) {
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
        if (Objects.isNull(selectedNode)) return;
        Object data = selectedNode.getUserObject();
        if (data instanceof CalculationSchema) {
            CalculationSchema selectedSchema = (CalculationSchema) data;
            File destination = gui.saveFileDialog(strings.getString("dialog.save.model_with_data"));
            if (Objects.isNull(destination)) return;
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
    public void setUI(GfemGUI gui) {
        this.gui = gui;
    }

    @Override
    public void deleteNode() {
        if (Objects.isNull(selectedNode)) return;
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

    /**
     * Function that reads current programme state and updates all view in accordance to that state.
     * Dependent elements are:
     * 1) Combo box
     * 2) Tree view
     */
    public void refresh() {
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
            if (Objects.isNull(shouldSelect)) shouldSelect = App.getInstance().EMPTY_MODEL;
            setComboboxSelected(shouldSelect);

            tableData = new ArrayList<>();
            for (Map.Entry<String, Object> e : selectedSchema.getInputData().asMap().entrySet()) {
                if (e.getValue() instanceof Number) gui.addInputNumericEntry(e.getKey(), (Number) e.getValue());
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
                for (Map.Entry<String, Object> e : selectedSchema.getResult().asMap().entrySet()) {
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


    private TableModel getTableModel() {
        return new ExcelTableModel(tableIndex, tableData);
    }

    private void addToTable(String key, HashMap<Number, Number> value) {
        Set<Number> indexSet = new HashSet<>();
        value.forEach((k, v) -> indexSet.add(k));
        tableIndex = new ArrayList<>();
        List<Object> tableRow = new ArrayList<>(Collections.singleton(key));
        indexSet.stream().sorted().forEach((k) -> {
            tableRow.add(value.get(k));
            tableIndex.add(k);
        });
        tableData.add(tableRow);
    }
}
