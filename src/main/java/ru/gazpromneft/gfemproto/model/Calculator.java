package ru.gazpromneft.gfemproto.model;

public class Calculator {
    public static CalculationSchema calculate(CalculationSchema schema) throws CalculationError {
        try {
            double startTime = System.currentTimeMillis();
            schema.model.setData(schema.data);
            OutputData result = schema.model.calculate();
            double endTime = System.currentTimeMillis();
            schema.setResult(result, endTime - startTime);
            schema.freezeToExcel();
            return schema;
        } catch (Exception e) {
            throw new CalculationError(e, schema);
        }
    }

}
