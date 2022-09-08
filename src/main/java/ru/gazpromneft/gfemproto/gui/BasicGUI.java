package ru.gazpromneft.gfemproto.gui;

import javax.swing.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicGUI extends JFrame {
    protected final Logger logger;
    public BasicGUI() {
        logger = Logger.getLogger(this.getClass().getName());
    }

    protected final JFileChooser fc = new JFileChooser();
    public static void updateLookAndFeel() {
        Logger logger = Logger.getLogger(BasicGUI.class.getName());
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
        Logger logger = Logger.getLogger(TinyGUI.class.getName());
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

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(new JFrame(), message, "Apache POI Demo",
                JOptionPane.INFORMATION_MESSAGE);
    }


    public void showError(String message) {
        JOptionPane.showMessageDialog(new JFrame(), message, "Ошибка",
                JOptionPane.ERROR_MESSAGE);
    }
}
