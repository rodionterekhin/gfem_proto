package ru.gazpromneft.gfemproto.model;

import java.io.Serializable;

public class CalculationSchema implements Serializable {
    protected final ExcelModel model;
    protected final InputData data;
    // In the future, we can customize schema by adding following lines:
    // Macroparameters macroparameters;
    // Parameters parameters;
    protected OutputData result;

    public CalculationSchema(ExcelModel model, InputData inputData) {
        this.data = inputData;
        this.model = model;
    }

    public CalculationSchema(InputData inputData) {
        this.data = inputData;
        this.model = inputData.getAttachedModel();
    }

    @Override
    public String toString() {
        return "[" + this.data + " -> " + this.model + "]";
    }

    protected void setResult(OutputData result) {
        this.result = result;
    }

    public OutputData getResult() {
        return result;
    }

    public boolean isCompleted() {
        return result != null;
    }
}
