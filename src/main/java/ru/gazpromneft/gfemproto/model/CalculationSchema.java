package ru.gazpromneft.gfemproto.model;

import javax.swing.*;
import java.io.Serializable;

public class CalculationSchema implements Serializable {
    protected ExcelModel model;
    protected InputData data;
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

    public OutputData getResult(OutputData result) {
        return result;
    }

    public boolean isCompleted() {
        return result != null;
    }
}
