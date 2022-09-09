package ru.gazpromneft.gfemproto.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import ru.gazpromneft.gfemproto.IMainController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class GfemGUI extends BasicGUI implements TreeSelectionListener {
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
    private JPanel inputArrayPanel;
    private JPanel outputNumericPanel;
    private JPanel outputArrayPanel;
    private JButton выгрузкаВЭксельButton;
    private JButton btnDeleteElement;
    private JButton btnCreateDuplicate;
    private JComboBox cmbSelectedModel;
    private JScrollPane treeScrollablePane;
    private JPanel statusBarPanel;
    private JLabel lblStatus;

    private JMenuBar menuBar;
    private JMenu mnuFile, mnuEdit;
    private JMenuItem mniAbout, mniExit;
    private JMenuItem mniLoadModel, mniLoadData;
    private HashMap<String, PanelFiller> panelFillers = new HashMap<>();


    public GfemGUI(IMainController controller) {
        this.controller = controller;
        setTitle("Apache POI Demo");
        setContentPane(mainPanel);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        menuBar = new JMenuBar();
        mnuFile = new JMenu("Файл");
        mniAbout = new JMenuItem("О программе");
        mniExit = new JMenuItem("Выход");
        mniExit.addActionListener((ActionEvent ae) -> controller.exit());
        mniAbout.addActionListener((ActionEvent ae) -> controller.about());
        mnuFile.add(mniAbout);
        mnuFile.add(mniExit);

        mnuEdit = new JMenu("Правка");
        mniLoadModel = new JMenuItem("Загрузить модель...");
        mniLoadModel.addActionListener((ActionEvent ae) -> controller.loadModel());
        mniLoadData = new JMenuItem("Загрузить данные...");
        mniLoadData.addActionListener((ActionEvent ae) -> controller.loadCase());
        mnuEdit.add(mniLoadModel);
        mnuEdit.add(mniLoadData);

        menuBar.add(mnuFile);
        menuBar.add(mnuEdit);
        setJMenuBar(menuBar);
        setMinimumSize(new Dimension(800, 500));
        setSize(new Dimension(800, 500));
        btnAddModel.addActionListener((ActionEvent e) -> controller.loadModel());
        btnAddCase.addActionListener((ActionEvent e) -> controller.loadCase());
        btnCalculate.addActionListener((ActionEvent e) -> controller.calculate());
        btnCreateDuplicate.addActionListener((ActionEvent e) -> controller.duplicateNode());
        btnDeleteElement.addActionListener((ActionEvent e) -> controller.deleteNode());
        treeDataStructure.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeDataStructure.addTreeSelectionListener(this);
        inputNumericPanel.setLayout(new SpringLayout());
        inputArrayPanel.setLayout(new SpringLayout());
        outputNumericPanel.setLayout(new SpringLayout());
        outputArrayPanel.setLayout(new SpringLayout());
        cmbSelectedModel.addActionListener((ActionEvent e) -> controller.changedModel());

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
        inputNumericPanel.revalidate();
        inputNumericPanel.repaint();
        inputArrayPanel.revalidate();
        inputArrayPanel.repaint();
    }

    public void clearOutputEntries() {
        panelFillers.get("OutputNumeric").clearEntries();
        panelFillers.get("InputNumeric").clearEntries();
    }

    public void addInputNumericEntry(String name, String value) {
        panelFillers.get("InputNumeric").addNumericEntry(name, value);
    }

    public void addInputArrayEntry(String name, HashMap<Double, Double> value) {
        panelFillers.get("InputArray").addArrayEntry(name, value);
    }

    public void addOutputNumericEntry(String name, String value) {
        JLabel l = new JLabel(name, JLabel.TRAILING);
        outputNumericPanel.add(l);
        JTextField textField = new JTextField();
        textField.setText(value);
        l.setLabelFor(textField);
        outputNumericPanel.add(textField);
    }

    public void addOutputArrayEntry(String name, String value) {

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
        label1.setText("Числовые параметры");
        inputPanel.add(label1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Массивы");
        inputPanel.add(label2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        inputPanel.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer2 = new Spacer();
        inputPanel.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 10), new Dimension(200, 10), new Dimension(200, 10), 0, false));
        inputNumericPanel = new JPanel();
        inputNumericPanel.setLayout(new BorderLayout(0, 0));
        inputPanel.add(inputNumericPanel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        inputArrayPanel = new JPanel();
        inputArrayPanel.setLayout(new BorderLayout(0, 0));
        inputPanel.add(inputArrayPanel, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        separator1.setOrientation(1);
        inputPanel.add(separator1, new GridConstraints(1, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        inputPanel.add(spacer3, new GridConstraints(1, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(3, -1), new Dimension(3, -1), new Dimension(3, -1), 0, false));
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane.addTab("Результаты расчета", resultsPanel);
        final JLabel label3 = new JLabel();
        label3.setText("Числовые параметры");
        resultsPanel.add(label3, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Массивы");
        resultsPanel.add(label4, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        resultsPanel.add(spacer4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, 1, new Dimension(200, 10), new Dimension(200, 10), new Dimension(200, 10), 0, false));
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
        btnCalculate.setText("Рассчитать");
        panel1.add(btnCalculate, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel1.add(spacer7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel1.add(spacer8, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel1.add(spacer9, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        выгрузкаВЭксельButton = new JButton();
        выгрузкаВЭксельButton.setText("Выгрузка в эксель...");
        panel1.add(выгрузкаВЭксельButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        separator3.setOrientation(1);
        mainPanel.add(separator3, new GridConstraints(1, 2, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        dataStructurePanel = new JPanel();
        dataStructurePanel.setLayout(new GridLayoutManager(7, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(dataStructurePanel, new GridConstraints(2, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(300, -1), new Dimension(300, -1), new Dimension(300, -1), 0, false));
        btnAddModel = new JButton();
        btnAddModel.setText("Добавить модель...");
        dataStructurePanel.add(btnAddModel, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnAddCase = new JButton();
        btnAddCase.setText("Добавить кейс...");
        dataStructurePanel.add(btnAddCase, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        dataStructurePanel.add(spacer10, new GridConstraints(1, 0, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, 1, new Dimension(5, -1), new Dimension(5, -1), new Dimension(5, -1), 0, false));
        final Spacer spacer11 = new Spacer();
        dataStructurePanel.add(spacer11, new GridConstraints(3, 2, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, 1, new Dimension(5, -1), new Dimension(5, -1), new Dimension(5, -1), 0, false));
        final Spacer spacer12 = new Spacer();
        dataStructurePanel.add(spacer12, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        btnDeleteElement = new JButton();
        btnDeleteElement.setText("Удалить элемент");
        dataStructurePanel.add(btnDeleteElement, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCreateDuplicate = new JButton();
        btnCreateDuplicate.setText("Дублировать элемент");
        dataStructurePanel.add(btnCreateDuplicate, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator4 = new JSeparator();
        dataStructurePanel.add(separator4, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 5), new Dimension(-1, 5), new Dimension(-1, 5), 0, false));
        treeScrollablePane = new JScrollPane();
        dataStructurePanel.add(treeScrollablePane, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
        label5.setText("Модель для расчета:");
        mainPanel.add(label5, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbSelectedModel = new JComboBox();
        cmbSelectedModel.setName("Модель");
        mainPanel.add(cmbSelectedModel, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer16 = new Spacer();
        mainPanel.add(spacer16, new GridConstraints(1, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 2), new Dimension(-1, 2), new Dimension(-1, 2), 0, false));
        statusBarPanel = new JPanel();
        statusBarPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(statusBarPanel, new GridConstraints(4, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 20), new Dimension(-1, 20), new Dimension(-1, 20), 0, false));
        statusBarPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblStatus = new JLabel();
        lblStatus.setText("Готово");
        statusBarPanel.add(lblStatus, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer17 = new Spacer();
        statusBarPanel.add(spacer17, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JSeparator separator5 = new JSeparator();
        mainPanel.add(separator5, new GridConstraints(0, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        controller.treeSelectionChanged(treeDataStructure.getLastSelectedPathComponent());
    }

    public void setStatus(String status) {
        lblStatus.setText(status);
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
}
