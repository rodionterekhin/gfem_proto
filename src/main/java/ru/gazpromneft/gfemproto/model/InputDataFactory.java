package ru.gazpromneft.gfemproto.model;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputDataFactory {
    private static Logger logger = Logger.getLogger(ExcelModelFactory.class.getName());

    public static InputData fromFile(File file) throws InputDataCreationException {
        try {
            String path = file.getAbsolutePath();
            InputStream fis = new FileInputStream(path);
            Workbook wb;
            if (path.endsWith(".xlsx") || path.endsWith(".xls")) {
                wb = WorkbookFactory.create(fis);
            } else throw new InputDataCreationException("Неизвестное расширение файла (допустимые - .xlsx и .xls)!");
            return fromSheet(wb.getSheetAt(0));
        } catch (IOException exception) {
            logger.log(Level.SEVERE, "Cannot open file!");
            throw new InputDataCreationException("Невозможно открыть файл!");
        } catch (EncryptedDocumentException exception) {
            logger.log(Level.SEVERE, "The file is encrypted!");
            throw new InputDataCreationException("Файл защищен паролем!");
        }
    }

    public static InputData fromSheet(Sheet sheet) throws InputDataCreationException {
        validate(sheet);
        return new InputData(parseToMap(sheet));
    }

    private static HashMap<String, Object> parseToMap(Sheet sheet) {
        return null;
    }

    private static void validate(Sheet sheet) throws InputDataCreationException {
        Map<String, String> map = new HashMap<>();

        map.put("A1", "Тип");
        map.put("B1", "Имя");
        map.put("C1", "Значения");

        boolean correct = true;
        //check input sheet format
        for (Map.Entry<String, String> e : map.entrySet()) {
            CellAddress addr = new CellAddress(e.getKey());
            int row = addr.getRow();
            int col = addr.getColumn();
            Cell cell = sheet.getRow(row).getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            correct &= (Objects.equals(cell.getRichStringCellValue().getString(), e.getValue()));
        }

        if (!correct)
            throw new InputDataCreationException("На листе должны быть колонки" +
                    "\"Тип\", \"Имя\" and \"Значения\" в ячейках A1:C1");
    }
}
