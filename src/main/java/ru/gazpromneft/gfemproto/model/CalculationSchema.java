package ru.gazpromneft.gfemproto.model;

import ru.gazpromneft.gfemproto.Conventions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class CalculationSchema implements Serializable {
    protected ExcelModel model;
    protected final InputData data;
    // In the future, we can customize schema by adding following lines:
    // macroparameters
    // parameters
    protected OutputData result;
    private double calculationTime;
    private byte[] workbookCaptureByteArray;

    public CalculationSchema(ExcelModel model, InputData inputData) {
        this.data = inputData;
        this.model = model;
    }

    @Override
    public String toString() {
        if (model != null)
            return "" + this.data + ": " + this.model + "";
        else
            return "" + this.data + ": " + Conventions.EMPTY_MODEL;
    }

    protected void setResult(OutputData result, double calculationTime) {
        this.result = result;
        this.calculationTime = calculationTime;
    }

    public OutputData getResult() {
        return result;
    }

    public boolean isCompleted() {
        return result != null;
    }

    public double getCalculationTime() {
        return calculationTime;
    }

    public InputData getInputData() {
        return data;
    }

    public ExcelModel getModel() {
        return model;
    }

    public void setModel(ExcelModel model) {
        if (this.model != model) {
            this.result = null;
            this.model = model;
        }

    }

    protected void freezeToExcel() {
        if (!isCompleted()) {
            return;
        } else {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                this.model.workbook.get().write(out);
                out.flush();
                out.close();
                this.workbookCaptureByteArray = out.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveToFile(File file) throws IOException {
        if (!isCompleted()) {
            return;
        } else {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(workbookCaptureByteArray);
            fos.close();
        }
    }
}
