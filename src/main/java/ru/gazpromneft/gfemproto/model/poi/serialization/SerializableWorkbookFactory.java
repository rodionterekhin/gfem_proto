package ru.gazpromneft.gfemproto.model.poi.serialization;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.Serializable;

public class SerializableWorkbookFactory {
    public static SerializableWorkbook fromWorkbook(Workbook wb) {
        if (wb instanceof HSSFWorkbook) {
            return new SerializableHSSFWorkbook((HSSFWorkbook) wb);

        } else if (wb instanceof XSSFWorkbook) {
            return new SerializableXSSFWorkbook((XSSFWorkbook) wb);

        }
        throw new IllegalArgumentException("Workbook not a XSSF or HSSF Workbook");
    }
}
