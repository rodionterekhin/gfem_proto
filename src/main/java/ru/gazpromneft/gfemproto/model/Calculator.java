package ru.gazpromneft.gfemproto.model;

// TODO public class ThreadedCalculator
public class Calculator {
    public static CalculationSchema calculate(CalculationSchema schema) throws CalculationError {
        try {
            schema.model.setData(schema.data);
            schema.setResult(schema.model.calculate());
            return schema;
        } catch (Exception e) {
            throw new CalculationError(e, schema);
        }
    }

    private static void calculationWorker() {
        // TODO Consider java.util.concurrent.Future usage
    }


}
