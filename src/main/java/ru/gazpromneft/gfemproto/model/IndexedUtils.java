package ru.gazpromneft.gfemproto.model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.gazpromneft.gfemproto.Conventions.RELATIVE_TOLERANCE;

public class IndexedUtils {
    protected static List<Number> parseArray(Row r) {
        List<Number> ret = new ArrayList<>();
        for (Cell c : r) {
            if (c.getColumnIndex() < 2)
                continue;
            Number value = c.getNumericCellValue();
            if (Math.abs(value.intValue() - value.doubleValue()) < RELATIVE_TOLERANCE)
                ret.add(value.intValue());
            else
                ret.add(value.doubleValue());
        }
        return ret;
    }

    protected static void fillArray(Row r, List<Number> array) {
        AtomicInteger i = new AtomicInteger(2);
        array.forEach((d) ->
                r.getCell(i.getAndIncrement(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .setCellValue((d != null? d : 0).doubleValue()));
    }

    protected static List<Number> sequenceFromArray(List<Number> index, Map<Number, Number> values) {
        List<Number> list = new ArrayList<>();
        index.stream().map(values::get).forEach(list::add);
        return list;
    }
}
