package ru.gazpromneft.gfemproto.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import ru.gazpromneft.gfemproto.App;
import ru.gazpromneft.gfemproto.ExcelTreeModel;
import ru.gazpromneft.gfemproto.UTF8Control;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class GfemGUI extends BasicGUI {
    private final IMainController controller;
    private JTabbedPane tabbedPane;
    private JPanel mainPanel;
    private JTree treeDataStructure;
    private JButton btnCalculate;
    private JButton btnAddCase;
    private JButton btnAddModel;
    private JPanel inputPanel;
    private JPanel resultsPanel;
    private JPanel dataStructurePanel;
    private JPanel inputNumericPanel;
    private JPanel outputNumericPanel;
    private JPanel outputArrayPanel;
    private JButton btnSaveToExcel;
    private JButton btnDeleteElement;
    private JButton btnCreateDuplicate;
    private JComboBox cmbSelectedModel;
    private JScrollPane treeScrollablePane;
    private JPanel statusBarPanel;
    private JLabel lblStatus;
    private JPanel inputArrayPanel;
    private JScrollPane inputNumericScrollPane;
    private JScrollPane inputArrayScrollPane;

    private JMenuBar menuBar;
    private JMenu mnuFile, mnuEdit;
    private JMenuItem mniAbout, mniSupportedFunctions, mniExit;
    private JMenuItem mniLoadModel, mniLoadData;
    private HashMap<String, PanelFiller> panelFillers = new HashMap<>();
    private String statusPrefix = "";


    public GfemGUI(IMainController controller, Image icon) {
        this.controller = controller;
        controller.setUI(this);
        setTitle(App.getStrings().getString("application.name") + " " + App.getInstance().BUILD_VERSION);
        setContentPane(mainPanel);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(icon);

        menuBar = new JMenuBar();
        mnuFile = new JMenu(this.$$$getMessageFromBundle$$$("strings", "menu.file"));
        mniAbout = new JMenuItem(this.$$$getMessageFromBundle$$$("strings", "menu.file.about"));
        mniSupportedFunctions = new JMenuItem(this.$$$getMessageFromBundle$$$("strings", "menu.file.supported_functions"));
        mniExit = new JMenuItem(this.$$$getMessageFromBundle$$$("strings", "menu.file.exit"));
        mniExit.addActionListener((ActionEvent ae) -> this.dispose());
        mniAbout.addActionListener((ActionEvent ae) -> controller.about());
        mniSupportedFunctions.addActionListener((ActionEvent ae) -> controller.available_functions());
        mnuFile.add(mniAbout);
        mnuFile.add(mniSupportedFunctions);
        mnuFile.add(mniExit);

        mnuEdit = new JMenu(this.$$$getMessageFromBundle$$$("strings", "menu.edit"));
        mniLoadModel = new JMenuItem(this.$$$getMessageFromBundle$$$("strings", "open.model"));
        mniLoadModel.addActionListener((ActionEvent ae) -> controller.loadModel());
        mniLoadData = new JMenuItem(this.$$$getMessageFromBundle$$$("strings", "open.data"));
        mniLoadData.addActionListener((ActionEvent ae) -> controller.loadCase());
        mnuEdit.add(mniLoadModel);
        mnuEdit.add(mniLoadData);

        menuBar.add(mnuFile);
        menuBar.add(mnuEdit);
        setJMenuBar(menuBar);
        setMinimumSize(new Dimension(1200, 800));
        setSize(new Dimension(1200, 800));
        btnAddModel.addActionListener((ActionEvent e) -> controller.loadModel());
        btnAddCase.addActionListener((ActionEvent e) -> controller.loadCase());
        btnCalculate.addActionListener((ActionEvent e) -> controller.calculate());
        btnCreateDuplicate.addActionListener((ActionEvent e) -> controller.duplicateNode());
        btnDeleteElement.addActionListener((ActionEvent e) -> controller.deleteNode());
        treeDataStructure.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeDataStructure.addTreeSelectionListener((tsl) -> controller.treeSelectionChanged(treeDataStructure.getLastSelectedPathComponent()));
        inputNumericPanel.setLayout(new SpringLayout());
        inputArrayPanel.setLayout(new SpringLayout());
        outputNumericPanel.setLayout(new SpringLayout());
        outputArrayPanel.setLayout(new SpringLayout());
        cmbSelectedModel.addActionListener((ActionEvent e) -> controller.changedModel());
        treeScrollablePane.setViewportView(treeDataStructure);
        inputNumericScrollPane.setViewportView(inputNumericPanel);
        inputArrayScrollPane.setViewportView(inputArrayPanel);
        //inputNumericScrollPane.setViewportView(inputNumericPanel);
        // inputNumericScrollPane.setViewportView(inputNumericPanel);
        btnSaveToExcel.addActionListener((ActionEvent e) -> controller.saveToExcel());
        setupPanelFillers();
    }

    private void setupPanelFillers() {
        panelFillers.put("InputNumeric", new PanelFiller(inputNumericPanel));
        panelFillers.put("InputArray", new PanelFiller(inputArrayPanel));
        panelFillers.put("OutputNumeric", new PanelFiller(outputNumericPanel));
        panelFillers.put("OutputArray", new PanelFiller(outputArrayPanel));

    }

    public TreeModel getTreeModel() {
        return treeDataStructure.getModel();
    }

    public void setTreeModel(TreeModel treeModel) {
        treeDataStructure.setModel(treeModel);
    }

    public void setComboboxValues(Object... values) {
        DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>(values);
        cmbSelectedModel.setModel(model);
    }

    public void setComboboxSelected(Object value) {
        cmbSelectedModel.setSelectedItem(value);
        //cmbSelectedModel.updateUI();
    }

    public Object getComboboxSelected() {
        return cmbSelectedModel.getSelectedItem();
    }

    public void clearInputEntries() {
        while (inputArrayPanel.getComponentCount() > 0) {
            inputArrayPanel.remove(0);
        }
        while (inputNumericPanel.getComponentCount() > 0) {
            inputNumericPanel.remove(0);
        }
        //inputNumericPanel.revalidate();
        inputNumericPanel.repaint();
        //inputArrayPanel.revalidate();
        inputArrayPanel.repaint();
    }

    public void clearOutputEntries() {
        panelFillers.get("OutputNumeric").clearEntries();
        panelFillers.get("OutputNumeric").clearEntries();
        outputNumericPanel.revalidate();
        outputNumericPanel.repaint();
        outputArrayPanel.revalidate();
        outputArrayPanel.repaint();
    }

    public void addInputNumericEntry(String name, Number value) {
        panelFillers.get("InputNumeric").addNumericEntry(name, value);
    }

    public void addOutputNumericEntry(String name, Number value) {
        panelFillers.get("OutputNumeric").addNumericEntry(name, value);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(5, 6, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(3, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tabbedPane = new JTabbedPane();
        panel1.add(tabbedPane, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane.addTab("Исходные данные", inputPanel);
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, this.$$$getMessageFromBundle$$$("strings", "calculation.params"));
        inputPanel.add(label1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        this.$$$loadLabelText$$$(label2, this.$$$getMessageFromBundle$$$("strings", "calculation.entries"));
        inputPanel.add(label2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        inputPanel.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer2 = new Spacer();
        inputPanel.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(300, 10), new Dimension(300, 10), new Dimension(300, 10), 0, false));
        final JSeparator separator1 = new JSeparator();
        separator1.setOrientation(1);
        inputPanel.add(separator1, new GridConstraints(1, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        inputPanel.add(spacer3, new GridConstraints(1, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(3, -1), new Dimension(3, -1), new Dimension(3, -1), 0, false));
        inputNumericScrollPane = new JScrollPane();
        inputPanel.add(inputNumericScrollPane, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        inputNumericPanel = new JPanel();
        inputNumericPanel.setLayout(new BorderLayout(0, 0));
        inputNumericScrollPane.setViewportView(inputNumericPanel);
        inputArrayScrollPane = new JScrollPane();
        inputPanel.add(inputArrayScrollPane, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        inputArrayPanel = new JPanel();
        inputArrayPanel.setLayout(new BorderLayout(0, 0));
        inputArrayScrollPane.setViewportView(inputArrayPanel);
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane.addTab("Результаты расчета", resultsPanel);
        final JLabel label3 = new JLabel();
        this.$$$loadLabelText$$$(label3, this.$$$getMessageFromBundle$$$("strings", "calculation.kpe"));
        resultsPanel.add(label3, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        this.$$$loadLabelText$$$(label4, this.$$$getMessageFromBundle$$$("strings", "calculation.entries"));
        resultsPanel.add(label4, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        resultsPanel.add(spacer4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, 1, new Dimension(300, 10), new Dimension(300, 10), new Dimension(300, 10), 0, false));
        final Spacer spacer5 = new Spacer();
        resultsPanel.add(spacer5, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, 1, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        outputNumericPanel = new JPanel();
        outputNumericPanel.setLayout(new BorderLayout(0, 0));
        resultsPanel.add(outputNumericPanel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        outputArrayPanel = new JPanel();
        outputArrayPanel.setLayout(new BorderLayout(0, 0));
        resultsPanel.add(outputArrayPanel, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator2 = new JSeparator();
        separator2.setOrientation(1);
        resultsPanel.add(separator2, new GridConstraints(1, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        resultsPanel.add(spacer6, new GridConstraints(1, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(3, -1), new Dimension(3, -1), new Dimension(3, -1), 0, false));
        btnCalculate = new JButton();
        this.$$$loadButtonText$$$(btnCalculate, this.$$$getMessageFromBundle$$$("strings", "calculate"));
        panel1.add(btnCalculate, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel1.add(spacer7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel1.add(spacer8, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel1.add(spacer9, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        btnSaveToExcel = new JButton();
        btnSaveToExcel.setEnabled(false);
        this.$$$loadButtonText$$$(btnSaveToExcel, this.$$$getMessageFromBundle$$$("strings", "save.result.to.excel"));
        panel1.add(btnSaveToExcel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        separator3.setOrientation(1);
        mainPanel.add(separator3, new GridConstraints(1, 2, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        dataStructurePanel = new JPanel();
        dataStructurePanel.setLayout(new GridLayoutManager(7, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(dataStructurePanel, new GridConstraints(2, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(300, -1), new Dimension(300, -1), new Dimension(300, -1), 0, false));
        btnAddModel = new JButton();
        this.$$$loadButtonText$$$(btnAddModel, this.$$$getMessageFromBundle$$$("strings", "open.model"));
        dataStructurePanel.add(btnAddModel, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnAddCase = new JButton();
        this.$$$loadButtonText$$$(btnAddCase, this.$$$getMessageFromBundle$$$("strings", "open.data"));
        dataStructurePanel.add(btnAddCase, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        dataStructurePanel.add(spacer10, new GridConstraints(1, 0, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, 1, new Dimension(5, -1), new Dimension(5, -1), new Dimension(5, -1), 0, false));
        final Spacer spacer11 = new Spacer();
        dataStructurePanel.add(spacer11, new GridConstraints(3, 2, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, 1, new Dimension(5, -1), new Dimension(5, -1), new Dimension(5, -1), 0, false));
        final Spacer spacer12 = new Spacer();
        dataStructurePanel.add(spacer12, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        btnDeleteElement = new JButton();
        this.$$$loadButtonText$$$(btnDeleteElement, this.$$$getMessageFromBundle$$$("strings", "delete.element"));
        dataStructurePanel.add(btnDeleteElement, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCreateDuplicate = new JButton();
        this.$$$loadButtonText$$$(btnCreateDuplicate, this.$$$getMessageFromBundle$$$("strings", "duplicate.element"));
        dataStructurePanel.add(btnCreateDuplicate, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator4 = new JSeparator();
        dataStructurePanel.add(separator4, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 5), new Dimension(-1, 5), new Dimension(-1, 5), 0, false));
        treeScrollablePane = new JScrollPane();
        treeScrollablePane.setHorizontalScrollBarPolicy(30);
        treeScrollablePane.setVerticalScrollBarPolicy(20);
        dataStructurePanel.add(treeScrollablePane, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(298, -1), new Dimension(298, -1), new Dimension(298, -1), 0, false));
        treeScrollablePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        treeDataStructure = new JTree();
        treeDataStructure.setLargeModel(false);
        treeDataStructure.putClientProperty("html.disable", Boolean.FALSE);
        treeScrollablePane.setViewportView(treeDataStructure);
        final Spacer spacer13 = new Spacer();
        mainPanel.add(spacer13, new GridConstraints(2, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(2, -1), new Dimension(2, -1), new Dimension(2, -1), 0, false));
        final Spacer spacer14 = new Spacer();
        mainPanel.add(spacer14, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 2), new Dimension(-1, 2), new Dimension(-1, 2), 0, false));
        final Spacer spacer15 = new Spacer();
        mainPanel.add(spacer15, new GridConstraints(2, 5, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(5, -1), new Dimension(5, -1), new Dimension(5, -1), 0, false));
        final JLabel label5 = new JLabel();
        this.$$$loadLabelText$$$(label5, this.$$$getMessageFromBundle$$$("strings", "selected.model.label"));
        mainPanel.add(label5, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbSelectedModel = new JComboBox();
        mainPanel.add(cmbSelectedModel, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer16 = new Spacer();
        mainPanel.add(spacer16, new GridConstraints(1, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 2), new Dimension(-1, 2), new Dimension(-1, 2), 0, false));
        statusBarPanel = new JPanel();
        statusBarPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(statusBarPanel, new GridConstraints(4, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 20), new Dimension(-1, 20), new Dimension(-1, 20), 0, false));
        statusBarPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblStatus = new JLabel();
        statusBarPanel.add(lblStatus, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer17 = new Spacer();
        statusBarPanel.add(spacer17, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JSeparator separator5 = new JSeparator();
        mainPanel.add(separator5, new GridConstraints(0, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label5.setLabelFor(cmbSelectedModel);
    }

    private static Method $$$cachedGetBundleMethod$$$ = null;

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if ($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    public void setStatus(String status) {
        lblStatus.setText(statusPrefix + status);
    }

    public void setStatusPrefix(String prefix) {
        statusPrefix = prefix + " | ";
    }

    @Override
    public void dispose() {
        super.dispose();
        logger.log(Level.INFO, "Exit signal detected, calling controllers exit hook");
        controller.exit();
    }

    public List<Object> getComboboxValues() {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < cmbSelectedModel.getItemCount(); i++) {
            list.add(cmbSelectedModel.getModel().getElementAt(i));
        }
        return list;
    }

    public void focusOnOutput() {
        tabbedPane.setSelectedIndex(1);
    }

    public void updateAll() {
        ((ExcelTreeModel) getTreeModel()).refreshNodes();
        panelFillers.forEach((k, v) -> v.complete());
    }

    public void addInputTable(TableModel tableModel) {
        JTable table = new JTable(tableModel);

        int columnCount = table.getColumnModel().getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            table.getColumnModel().getColumn(i).setMinWidth(i == 0 ? 200 : 100);
            table.getColumnModel().getColumn(i).setWidth(i == 0 ? 200 : 100);
            table.getColumnModel().getColumn(i).setMaxWidth(i == 0 ? 200 : 100);
            table.getColumnModel().getColumn(i).setPreferredWidth(i == 0 ? 200 : 100);
        }
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);
        inputArrayScrollPane.setViewportView(table);

    }

    public void setBtnToExcelState(boolean b) {
        btnSaveToExcel.setEnabled(b);
    }
}
