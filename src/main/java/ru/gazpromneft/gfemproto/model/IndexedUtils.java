package ru.gazpromneft.gfemproto.model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    protected static void fillArray(Row r, List<Double> array) {
        AtomicInteger i = new AtomicInteger(2);
        array.forEach((d) ->
                r.getCell(i.getAndIncrement(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue(d != null? d : 0));
    }

    protected static List<Double> sequenceFromArray(List<Double> index, Map<Double, Double> values) {
        return index.stream().map(values::get).toList();
    }
}
