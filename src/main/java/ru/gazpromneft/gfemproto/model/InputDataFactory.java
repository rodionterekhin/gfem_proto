package ru.gazpromneft.gfemproto.model;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import ru.gazpromneft.gfemproto.Conventions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputDataFactory {
    private static final Logger logger = Logger.getLogger(ExcelModelFactory.class.getName());

    public static InputData fromFile(File file) throws InputDataLoadException {
        try {
            String path = file.getAbsolutePath();
            InputStream fis = new FileInputStream(path);
            Workbook wb;
            if (path.endsWith(".xlsx") || path.endsWith(".xls")) {
                wb = WorkbookFactory.create(fis);
            } else throw new InputDataLoadException("Неизвестное расширение файла (допустимые - .xlsx и .xls)!");
            return fromSheet(file.getName(), wb.getSheetAt(0));
        } catch (IOException exception) {
            logger.log(Level.SEVERE, "Cannot open file!");
            throw new InputDataLoadException("Невозможно открыть файл!");
        } catch (EncryptedDocumentException exception) {
            logger.log(Level.SEVERE, "The file is encrypted!");
            throw new InputDataLoadException("Файл защищен паролем!");
        }
    }

    public static InputData fromSheet(String name, Sheet sheet) throws InputDataLoadException {
        validate(sheet);
        return new InputData(name, parseToMap(sheet));
    }

    private static HashMap<String, Object> parseToMap(Sheet sheet) throws InputDataLoadException {
        List<Double> currentIndex = null;
        HashMap<String, Object> parseResult = new HashMap<>();
        for (Row r: sheet) {
            if (r.getRowNum() == 0)
                continue;
            String typeString = r.getCell(0).getStringCellValue();
            String name = r.getCell(1).getStringCellValue();
            if (typeString.equals("") && name.equals(""))
                continue;

            Conventions.VariableType type = Conventions.VariableType.fromText(typeString);
            if (Objects.isNull(type)) {
                String msg = "Неизвестный тип переменной \"" + name + "\": \"" + typeString + "\"";
                throw new InputDataLoadException(msg);
            }
            if (type == Conventions.VariableType.NUMERIC) {
                parseResult.put(name, r.getCell(2).getNumericCellValue());
            }
            else if (type == Conventions.VariableType.INDEX) {
                currentIndex = IndexedUtils.parseArray(r);
            }
            else if (type == Conventions.VariableType.ARRAY) {
                HashMap<Double, Double> array = new HashMap<>();
                List<Double> finalData = IndexedUtils.parseArray(r);
                if (Objects.isNull(currentIndex)) {
                    String msg = "Строка " +
                            (r.getRowNum() + 1) +
                            ":\n" +
                            "Массив не может идти перед индексом!";
                    throw new InputDataLoadException(msg);
                }
                List<Double> finalCurrentIndex = currentIndex;
                currentIndex.forEach((k) ->
                        array.put(k,
                                finalCurrentIndex.indexOf(k) < finalData.size()?
                                        finalData.get(finalCurrentIndex.indexOf(k)) : 0d));
                parseResult.put(name, array);
            }
        }
        return parseResult;
    }



    private static void validate(Sheet sheet) throws InputDataLoadException {
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
            throw new InputDataLoadException("На листе должны быть колонки" +
                    "\"Тип\", \"Имя\" and \"Значения\" в ячейках A1:C1");
    }
}
