package ru.gazpromneft.gfemproto.model;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.gazpromneft.gfemproto.Conventions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelModelFactory {

    private static Logger logger = Logger.getLogger(ExcelModelFactory.class.getName());

    public static ExcelModel fromFile(File file) throws ModelLoadException, ModelValidationException {
        try {
            String path = file.getAbsolutePath();
            InputStream fis = new FileInputStream(path);
            Workbook wb;
            if (path.endsWith(".xlsx") || path.endsWith(".xls")) {
                wb = WorkbookFactory.create(fis);
            } else throw new ModelLoadException("Неизвестное расширение файла (допустимые - .xlsx и .xls)!");
            return new ExcelModel(file.getName(), wb);
        } catch (IOException exception) {
            logger.log(Level.SEVERE, "Cannot open file!");
            throw new ModelLoadException("Невозможно прочитать файл!");
        } catch (EncryptedDocumentException exception) {
            logger.log(Level.SEVERE, "The file is encrypted!");
            throw new ModelLoadException("Файл зашифрован!");
        }
    }

    public static ExcelModel emptyModel() {
        String name = Conventions.EMPTY_MODEL;
        Workbook wb = new XSSFWorkbook();
        try {
            return new ExcelModel(name, wb) {

                @Override
                protected void validate(Workbook workbook) {
                    return;
                }

                @Override
                public void calculate() {
                    return;
                }
            };
        } catch (ModelValidationException e) {
            assert false;
            return null;
        }
    }
}
