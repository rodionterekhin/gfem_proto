package ru.gazpromneft.gfemproto.model;

import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import ru.gazpromneft.gfemproto.model.poi.serialization.SerializableWorkbook;
import ru.gazpromneft.gfemproto.model.poi.serialization.SerializableWorkbookFactory;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
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
        boolean overallCorrect = true;
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

    private void updateModelWithData(Workbook model, Workbook data) {
        Sheet dataSheet = data.getSheetAt(0);
        Sheet modelDataSheet = model.getSheet("input");

        for (Row r : dataSheet) {
            for (Cell sourceCell : r) {
                Cell targetCell = modelDataSheet.getRow(r.getRowNum())
                        .getCell(sourceCell.getColumnIndex());
                switch (sourceCell.getCellType()) {
                    case FORMULA -> targetCell.setCellFormula(sourceCell.getCellFormula());
                    case STRING -> targetCell.setCellValue(sourceCell.getStringCellValue());
                    case NUMERIC -> targetCell.setCellValue(sourceCell.getNumericCellValue());
                    case BLANK -> targetCell.setBlank();
                }
            }
        }
    }

    public void calculate() {
        FormulaEvaluator formulaEvaluator = this.workbook.get().getCreationHelper().createFormulaEvaluator();
        //updateModelWithData(this.workbook.get(), inputData);
        formulaEvaluator.clearAllCachedResultValues();
        long load_time = System.nanoTime();
        formulaEvaluator.evaluateAll();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                String.format("Workbook evaluation time is: %f mS%n",
                              (double) (System.nanoTime() - load_time) / 1e6));
    }

    public void getResults() {
        throw new NotImplementedException("Not implemented yet");
    }

    public InputData getData() {
        return inputData;
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

    public void release() {

    }

    @Override
    public String toString() {
        return name;
    }

    public void changeName(String name) {
        this.name = name;
    }
}
