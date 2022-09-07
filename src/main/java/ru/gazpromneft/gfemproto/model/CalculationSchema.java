package ru.gazpromneft.gfemproto.model;

import javax.swing.*;
import java.io.Serializable;

public class CalculationSchema implements Serializable {
    protected ExcelModel model;
    protected InputData data;
    // Macroparameters macroparameters;
    // Parameters parameters;

    public CalculationSchema(ExcelModel model, InputData inputData) {
        this.data = inputData;
        this.model = model;
    }
}
