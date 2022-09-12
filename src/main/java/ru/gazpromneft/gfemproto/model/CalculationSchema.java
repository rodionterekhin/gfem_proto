package ru.gazpromneft.gfemproto.model;

import ru.gazpromneft.gfemproto.Conventions;

import java.io.Serializable;

public class CalculationSchema implements Serializable {
    protected ExcelModel model;
    protected InputData data;
    // In the future, we can customize schema by adding following lines:
    // Macroparameters macroparameters;
    // Parameters parameters;
    protected OutputData result;
    private boolean actualResult;

    public CalculationSchema(ExcelModel model, InputData inputData) {
        this.data = inputData;
        this.model = model;
        this.actualResult = false;
    }

    public CalculationSchema(InputData inputData) {
        this.data = inputData;
        this.model = inputData.getAttachedModel();
    }

    @Override
    public String toString() {
        if (model != null)
            return "" + this.data + ": " + this.model + "";
        else
            return "" + this.data + ": " + Conventions.EMPTY_MODEL;
    }

    protected void setResult(OutputData result) {
        this.result = result;
        this.actualResult = true;
    }

    public OutputData getResult() {
        return result;
    }

    public boolean isCompleted() {
        return result != null;
    }

    public boolean isResultActual() {
        return this.actualResult;
    }

    public InputData getInputData() {
        return data;
    }

    public ExcelModel getModel() {
        return model;
    }

    public void setModel(ExcelModel model) {
        this.actualResult = false;
        this.model = model;
    }
}
