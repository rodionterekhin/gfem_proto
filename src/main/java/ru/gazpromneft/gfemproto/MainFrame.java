package ru.gazpromneft.gfemproto;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import java.awt.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class MainFrame extends JFrame {

    private final IMainController controller;
    private JTextField txtModel;
    private JButton btnModel;
    private JTextField txtData;
    private JButton btnData;
    private JButton btnCalculate;
    private JButton btnExit;
    private JLabel lblModel;
    private JLabel lblData;
    private JPanel mainPanel;
    final JFileChooser fc = new JFileChooser();

    public MainFrame(IMainController iController) {


        this.controller = iController;
        setTitle("Apache POI Demo");
        setContentPane(mainPanel);
        setResizable(false);
        pack();

        txtData.setEditable(false);
        txtModel.setEditable(false);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        btnExit.addActionListener((actionEvent) -> controller.exit());
        btnCalculate.addActionListener((actionEvent) -> controller.calculate());
        btnModel.addActionListener((actionEvent) -> txtModel.setText(controller.loadModel()));
        btnData.addActionListener((actionEvent) -> txtData.setText(controller.loadData()));
        setVisible(true);
    }

    public static void updateLookAndFeel() {
        Logger logger = Logger.getLogger(MainFrame.class.getName());
        try {
            logger.log(Level.INFO, "Trying to install system look and feel...");
            logger.log(Level.INFO, "Installing " + UIManager.getSystemLookAndFeelClassName() + " look and feel");
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException |
                 ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Cannot install system look and feel!");

        }
    }

    public File openFileDialog() {
        Logger logger = Logger.getLogger(MainFrame.class.getName());
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
            logger.log(Level.INFO, "Opening: " + file.getName() + ".");
            return file;
        } else {
            logger.log(Level.INFO, "Open command cancelled by user.");
            return null;
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(new JFrame(), message, "Ошибка",
                JOptionPane.ERROR_MESSAGE);
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
        mainPanel.setLayout(new GridLayoutManager(5, 8, new Insets(0, 0, 0, 0), -1, -1));
        lblModel = new JLabel();
        lblModel.setText("Файл модели:");
        mainPanel.add(lblModel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(95, 16), null, 0, false));
        txtModel = new JTextField();
        txtModel.setText("");
        mainPanel.add(txtModel, new GridConstraints(1, 2, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnModel = new JButton();
        btnModel.setText("Выбрать...");
        mainPanel.add(btnModel, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblData = new JLabel();
        lblData.setText("Файл данных:");
        mainPanel.add(lblData, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(95, 16), null, 0, false));
        txtData = new JTextField();
        mainPanel.add(txtData, new GridConstraints(2, 2, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnData = new JButton();
        btnData.setText("Выбрать...");
        mainPanel.add(btnData, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(3, 5, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(95, 11), null, 0, false));
        btnExit = new JButton();
        btnExit.setText("Выход");
        mainPanel.add(btnExit, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCalculate = new JButton();
        btnCalculate.setText("Расчет");
        mainPanel.add(btnCalculate, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        mainPanel.add(spacer3, new GridConstraints(2, 7, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), null, 0, false));
        final Spacer spacer4 = new Spacer();
        mainPanel.add(spacer4, new GridConstraints(1, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), new Dimension(10, -1), null, 0, false));
        final Spacer spacer5 = new Spacer();
        mainPanel.add(spacer5, new GridConstraints(0, 1, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), null, 0, false));
        final Spacer spacer6 = new Spacer();
        mainPanel.add(spacer6, new GridConstraints(4, 1, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), new Dimension(-1, 10), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
