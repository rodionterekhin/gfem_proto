package ru.gazpromneft.gfemproto.model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

public class IndexedUtils {
    protected static List<Double> parseArray(Row r) {
        List<Double> ret = new ArrayList<>();
        for (Cell c : r) {
            if (c.getColumnIndex() < 2)
                continue;
            ret.add(c.getNumericCellValue());
        }
        return ret;
    }
}
