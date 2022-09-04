package ru.gazpromneft.gfemproto;

import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Fixed3ArgFunction;

public final class SLN extends Fixed3ArgFunction  {

    // calculates deprecation SLN(cost, salvage, life) as described in
    // https://corporatefinanceinstitute.com/resources/excel/functions/straight-line-sln-function-depreciation/
    //
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2)
    {
        try{
            final double cost    = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex));
            final double salvage = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(arg1, srcRowIndex, srcColumnIndex));
            final double life    = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(arg2, srcRowIndex, srcColumnIndex));
            return new NumberEval((cost - salvage) / life);
        } catch (EvaluationException e){
            return e.getErrorEval();
        }
    }
}