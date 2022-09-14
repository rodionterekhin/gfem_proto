package ru.gazpromneft.gfemproto.model;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.gazpromneft.gfemproto.Conventions;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class OutputDataFactory {

    protected static OutputData fromModel(ExcelModel model) throws OutputDataCreationException {
        return new OutputData(parseToMap(model.workbook.get().getSheet("output")));
    }


    private static HashMap<String, Object> parseToMap(Sheet sheet) throws OutputDataCreationException {
        List<Number> currentIndex = null;
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
                throw new OutputDataCreationException(msg);
            }
            if (type == Conventions.VariableType.NUMERIC) {
                parseResult.put(name, r.getCell(2).getNumericCellValue());
            }
            else if (type == Conventions.VariableType.INDEX) {
                currentIndex = IndexedUtils.parseArray(r);
            }
            else if (type == Conventions.VariableType.ARRAY) {
                HashMap<Number, Number> array = new HashMap<>();
                List<Number> finalData = IndexedUtils.parseArray(r);
                if (Objects.isNull(currentIndex)) {
                    String msg = "Строка " +
                            (r.getRowNum() + 1) +
                            ":\n" +
                            "Массив не может идти перед индексом!";
                    throw new OutputDataCreationException(msg);
                }
                List<Number> finalCurrentIndex = currentIndex;
                currentIndex.forEach((k) ->
                        array.put(k,
                                finalCurrentIndex.indexOf(k) < finalData.size()?
                                        finalData.get(finalCurrentIndex.indexOf(k)) : 0d));
                parseResult.put(name, array);
            }
        }
        return parseResult;
    }

}
