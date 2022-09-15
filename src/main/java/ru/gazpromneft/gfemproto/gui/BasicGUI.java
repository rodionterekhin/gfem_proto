package ru.gazpromneft.gfemproto.gui;

import ru.gazpromneft.gfemproto.Conventions;
import ru.gazpromneft.gfemproto.UTF8Control;

import javax.swing.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicGUI extends JFrame {
    protected final Logger logger;
    public BasicGUI() {
        setTitle(ResourceBundle.getBundle("strings", new UTF8Control()).getString("application.name"));
        logger = Logger.getLogger(this.getClass().getName());
        fc.addChoosableFileFilter(excelFileFilter);
        fc.setAcceptAllFileFilterUsed(false);
    }

    protected final JFileChooser fc = new JFileChooser();

    private final FileFilter excelFileFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) return true;
            String extension = null;
            String path = f.getName();
            int i = path.lastIndexOf('.');

            if (i > 0 &&  i < path.length() - 1) {
                extension = path.substring(i+1).toLowerCase();
            }
            if (Objects.isNull(extension))
                return false;
            return extension.equals("xlsx") || extension.equals("xlsm");
        }

        @Override
        public String getDescription() {
            return ResourceBundle.getBundle("strings", new UTF8Control()).getString("dialog.filter.description");
        }
    };

    public File openFileDialog(String title) {
        Logger logger = Logger.getLogger(TinyGUI.class.getName());
        fc.setDialogTitle(title);
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
        JOptionPane.showMessageDialog(new JFrame(),
                message,
                ResourceBundle.getBundle("strings", new UTF8Control()).getString("application.name"),
                JOptionPane.INFORMATION_MESSAGE);
    }


    public void showError(String message) {
        JOptionPane.showMessageDialog(new JFrame(), message, "Ошибка",
                JOptionPane.ERROR_MESSAGE);
    }

    public File saveFileDialog(String title) {
        Logger logger = Logger.getLogger(TinyGUI.class.getName());
        fc.setDialogTitle(title);
        int returnVal = fc.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
            logger.log(Level.INFO, "Saving: " + file.getName() + ".");
            return file;
        } else {
            logger.log(Level.INFO, "Save command cancelled by user.");
            return null;
        }
    }
}
