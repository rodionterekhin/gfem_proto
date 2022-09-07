package ru.gazpromneft.gfemproto.model;

// TODO public class ThreadedCalculator
public class Calculator {
    public static void calculate(CalculationSchema calculationSchema) {
        calculationSchema.model.setData(calculationSchema.data);
        calculationSchema.model.calculate();

    }

    private static void calculationWorker() {
        // TODO Consider java.util.concurrent.Future usage
    }


}
