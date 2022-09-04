package ru.gazpromneft.gfemproto;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class MainFrame extends JFrame{

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
            logger.log(Level.INFO, "Installing "+ UIManager.getSystemLookAndFeelClassName() + " look and feel");
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException |
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
}
