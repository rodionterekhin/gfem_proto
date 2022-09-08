package ru.gazpromneft.gfemproto.model;

public class CalculationError extends RuntimeException{
    private final CalculationSchema schema;
    private final Throwable trueReason;

    public CalculationError(Throwable trueReason, CalculationSchema onSchema) {
        super(trueReason.getMessage());
        this.trueReason = trueReason;
        this.schema = onSchema;
    }

    public CalculationSchema getSchema() {
        return schema;
    }

    public Throwable getTrueReason() {
        return trueReason;
    }
}
