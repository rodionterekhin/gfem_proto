package ru.gazpromneft.gfemproto.model;

import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.SheetUtil;
import ru.gazpromneft.gfemproto.Conventions;
import ru.gazpromneft.gfemproto.model.poi.serialization.SerializableWorkbook;
import ru.gazpromneft.gfemproto.model.poi.serialization.SerializableWorkbookFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ExcelModel implements Serializable {

    protected InputData inputData;
    protected final SerializableWorkbook workbook;

    protected String name;

    public ExcelModel(String name, Workbook workbook) throws ModelValidationException {
        this.name = name;
        validate(workbook);
        try {
            inputData = extractInputData(name, workbook);
        } catch (InputDataLoadException e) {
            assert false;
        }
        this.workbook = SerializableWorkbookFactory.fromWorkbook(workbook);

    }

    protected void validate(Workbook workbook) throws ModelValidationException {

        boolean localCorrect;
        boolean overallCorrect;
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Несоответствие формату:");

        // check sheets available
        localCorrect = (workbook.getSheet("input") != null);
        overallCorrect = localCorrect;
        if (!localCorrect)
            errorMessage.append("\nВ книге нет листа \"input\"");

        localCorrect = (workbook.getSheet("output") != null);
        overallCorrect &= localCorrect;
        if (!localCorrect)
            errorMessage.append("\nВ книге нет листа \"output\"!");

        if (!overallCorrect){
            throw new ModelValidationException(errorMessage.toString());
        }

        Sheet input = workbook.getSheet("input");
        Sheet output = workbook.getSheet("output");

        Map<String, String> map = new HashMap<>();

        map.put("A1", "Тип");
        map.put("B1", "Имя");
        map.put("C1", "Значения");

        //check input sheet format
        for (Map.Entry<String, String> e : map.entrySet()) {
            CellAddress addr = new CellAddress(e.getKey());
            int row = addr.getRow();
            int col = addr.getColumn();
            Cell cell = input.getRow(row).getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            localCorrect &= (Objects.equals(cell.getRichStringCellValue().getString(), e.getValue()));
        }
        overallCorrect = localCorrect;

        if (!localCorrect)
            errorMessage.append("\nНа листе \"input\" должны быть колонки" +
                    "\"Тип\", \"Имя\" and \"Значения\" в ячейках A1:C1");

        map.put("C1", "Ссылки");

        //check output sheet format
        for (Map.Entry<String, String> e : map.entrySet()) {
            CellAddress addr = new CellAddress(e.getKey());
            int row = addr.getRow();
            int col = addr.getColumn();
            Cell cell = output.getRow(row).getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            localCorrect &= (Objects.equals(cell.getRichStringCellValue().getString(), e.getValue()));
        }
        overallCorrect &= localCorrect;

        if (!localCorrect)
            errorMessage.append("\nНа листе \"output\" должны быть колонки" +
                    "\"Тип\", \"Имя\" and \"Ссылки\" в ячейках A1:C1");

        if (!overallCorrect){
            throw new ModelValidationException(errorMessage.toString());
        }
    }

    protected InputData extractInputData(String name, Workbook workbook) throws InputDataLoadException {
        return InputDataFactory.fromSheet(name, workbook.getSheet("input"));
    }

    private void updateModelWithData(InputData data) {

        List<Number> currentIndex = null;

        Sheet modelDataSheet = this.workbook.get().getSheet("input");

        // Clear cell contents - move to another function
        for (Row r : modelDataSheet) {
            if (r.getRowNum() == 0)
                continue;
            Conventions.VariableType type = Conventions.VariableType.fromText(r.getCell(0).getStringCellValue());
            if (type == Conventions.VariableType.INDEX)
                continue;
            for (Cell c : r) {
                if (c.getColumnIndex() <= 2)
                    continue;
                SheetUtil.getCell(modelDataSheet,r.getRowNum(), c.getColumnIndex()).setBlank();
            }
        }

        for (Row r : modelDataSheet) {
            if (r.getRowNum() == 0)
                continue;
            String typeString = r.getCell(0).getStringCellValue();
            String name = r.getCell(1).getStringCellValue();
            if (typeString.equals("") && name.equals(""))
                continue;

            Conventions.VariableType type = Conventions.VariableType.fromText(typeString);
            assert !Objects.isNull(type);  // Мы уже прошли валидацию книги, все типы должны быть в порядке
            if (type != Conventions.VariableType.INDEX && Objects.isNull(data.asMap().get(name))) {
                Logger.getLogger(getClass().getName()).warning(String.format("В данных нет показателя \"%s\"", name));
                continue;
            }
            if (type == Conventions.VariableType.NUMERIC) {
                r.getCell(2).setCellValue(((Number) data.asMap().get(name)).doubleValue());
            }
            else if (type == Conventions.VariableType.INDEX) {
                currentIndex = IndexedUtils.parseArray(r);
            }
            else if (type == Conventions.VariableType.ARRAY) {
                assert currentIndex != null;
                Object value = data.asMap().get(name);
                if (value instanceof Map<?, ?>) {
                    Map<Number, Number> values = (Map<Number, Number>) value;
                    IndexedUtils.fillArray(r, IndexedUtils.sequenceFromArray(currentIndex, values));
                }
            }
        }
    }

    protected OutputData calculate() throws OutputDataCreationException{
        FormulaEvaluator formulaEvaluator = this.workbook.get().getCreationHelper().createFormulaEvaluator();
        updateModelWithData(inputData);
        formulaEvaluator.clearAllCachedResultValues();
        long load_time = System.nanoTime();
        formulaEvaluator.evaluateAll();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                String.format("Workbook evaluation time is: %f mS",
                              (System.nanoTime() - load_time) / 1e6));

        Sheet output = this.workbook.get().getSheet("output");

        StringBuilder report = new StringBuilder();
        for (Row r : output) {
            if (r.getRowNum() == 0)
                continue;
            String outputType = r.getCell(0).getStringCellValue();
            String outputName = r.getCell(1).getStringCellValue();
            double outputValue = r.getCell(2).getNumericCellValue();
            report.append(outputName)
                    .append(" = ")
                    .append(outputValue)
                    .append("\n");
        }
        try {
            FileOutputStream out = new FileOutputStream("result"+System.currentTimeMillis() + ".xlsx");
            this.workbook.get().write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputData result = OutputDataFactory.fromModel(this);
        result.setTextReport(report.toString());
        return result;
    }

    public void getResults() {
        throw new NotImplementedException("Not implemented yet");
    }

    public void setData(InputData inputData) {
        this.inputData = inputData;
    }

    public HashMap<String, Type> getInputDescriptor() {
        throw new NotImplementedException("Not implemented yet");
    }

    public HashMap<String, Type> getOutputDescriptor() {
        throw new NotImplementedException("Not implemented yet");
    }

    public boolean canUseInputData(InputData inputData) {
        return getInputDescriptor().equals(inputData.getDescriptor());
    }

    @Override
    public String toString() {
        return name;
    }

    public void changeName(String name) {
        this.name = name;
    }
}
