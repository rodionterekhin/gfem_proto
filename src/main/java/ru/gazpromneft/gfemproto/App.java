package ru.gazpromneft.gfemproto;

import com.formdev.flatlaf.FlatIntelliJLaf;
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import ru.gazpromneft.gfemproto.gui.GfemGUI;
import ru.gazpromneft.gfemproto.gui.IMainController;

import javax.swing.*;
import java.awt.*;
import java.io.*;

import java.net.JarURLConnection;
import java.net.URL;
import java.nio.channels.OverlappingFileLockException;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creation-Time: 14:57
 * Creation-Date: 02/09/2022
 * Author: Terekhin Rodion
 * Author-Email: rodionterekhin@gmail.com
 */

public class App {

    private static final int LOCK_WAITING_PERIOD = 1;       // seconds
    private static final int TOTAL_LOCK_WAITING_TIME = 15;  // seconds
    private static final int TOTAL_LOCK_READING_WAITING_TIME = 5;  // seconds
    public final Object EMPTY_MODEL = Conventions.EMPTY_MODEL;

    private final GfemGUI gui;
    private final Logger logger;

    private boolean canUseStateFile = true;
    private static final ResourceBundle strings = ResourceBundle.getBundle("strings", new UTF8Control());
    private Image appIcon = null;
    private IMainController controller = null;
    private static App instance = null;
    public final String BUILD_TIME;
    public final String BUILD_VERSION;

    public App() {
        instance = this;
        logger = Logger.getLogger(this.getClass().getName());

        Attributes manifestEntries = getManifestAttributes();
        if (manifestEntries != null) {
            BUILD_TIME = manifestEntries.getValue("Build-Time");
            BUILD_VERSION = manifestEntries.getValue("Build-Label");
        } else {
            BUILD_TIME = "undefined";
            BUILD_VERSION = "undefined";
        }

        ExcelTreeSaver.configure(LOCK_WAITING_PERIOD, TOTAL_LOCK_WAITING_TIME, TOTAL_LOCK_READING_WAITING_TIME);

        FlatIntelliJLaf.setup();
        Image icon = getAppIcon();
        tryRegisterSLN();
        controller = new MainController();
        gui = new GfemGUI(controller, icon);
        //
        tryLoadState();
        SwingUtilities.invokeLater(controller::refresh);
        gui.setVisible(true);
        gui.setStatus(strings.getString("status.idle"));
    }

    private Attributes getManifestAttributes() {
        String className = getClass().getSimpleName() + ".class";
        String classPath = Objects.requireNonNull(getClass().getResource(className)).toString();
        if (!classPath.startsWith("jar")) {
            return null;
        }
        try {
            URL url = new URL(classPath);
            JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
            Manifest manifest = jarConnection.getManifest();
            return manifest.getMainAttributes();
        } catch (IOException e) {
            return null;
        }
    }

    public static App getInstance() {
        return instance;
    }

    public static ResourceBundle getStrings() {
        return strings;
    }

    private void tryLoadState() {
        if (loadStateExists()) {
            try {
                gui.setTreeModel(loadState());
            } catch (OverlappingFileLockException e) {
                gui.showError(strings.getString("state.error.locked"));
                gui.setStatusPrefix(strings.getString("state.error.status_prefix"));
                logger.log(Level.SEVERE, e.getMessage(), e);
                canUseStateFile = false;
                gui.setTreeModel(new ExcelTreeModel());
            } catch (IOException e) {
                gui.showError(strings.getString("state.error.corrupt"));
                logger.log(Level.SEVERE, e.getMessage(), e);
                gui.setTreeModel(new ExcelTreeModel());
            } catch (ClassNotFoundException e) {
                gui.showError(strings.getString("state.error.classes"));
                logger.log(Level.SEVERE, e.getMessage(), e);
                gui.setTreeModel(new ExcelTreeModel());
            }
        } else {
            logger.info("No state file found. Assuming this is the first time the application is opened");
            gui.setTreeModel(new ExcelTreeModel());
        }
    }

    public Image getAppIcon() {
        if (Objects.isNull(appIcon)) appIcon = createAppIcon();
        return appIcon;
    }

    private Image createAppIcon() {
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
