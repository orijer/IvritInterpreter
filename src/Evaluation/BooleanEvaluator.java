package Evaluation;

import Variables.BooleanVariable;
import Variables.NumericVariable;
import Variables.VariablesFactory;

/**
 * An evaluator that specializes in evaluating boolean expressions.
 */
public class BooleanEvaluator extends OrderedEvaluator {

    @Override
    protected String evaluateComponents(String operation, String data1, String data2) {
        BooleanVariable resultVariable;

        switch (operation) {
            case "שווה":
                if (data1.equals(data2))
                    resultVariable = new BooleanVariable("אמת");
                else
                    resultVariable = new BooleanVariable("שקר");
                break;

            case "לא-שווה":
                if (!data1.equals(data2))
                    resultVariable = new BooleanVariable("אמת");
                else
                    resultVariable = new BooleanVariable("שקר");
                break;

            case "וגם":
                if (data1.equals("שקר") || data2.equals("שקר"))
                    resultVariable = new BooleanVariable("שקר");
                else
                    resultVariable = new BooleanVariable("אמת");
                break;

            case "או":
                if (data1.equals("אמת") || data2.equals("אמת"))

                    resultVariable = new BooleanVariable("אמת");
                else
                    resultVariable = new BooleanVariable("שקר");
                break;

            case ">":
                NumericVariable firstVariable = VariablesFactory.createNumericVariableByValue(data2);
                resultVariable = firstVariable.greaterThen(data1);
                break;

            case "<":
                firstVariable = VariablesFactory.createNumericVariableByValue(data2);
                resultVariable = firstVariable.lessThen(data1);
                break;

            default:
                throw new UnsupportedOperationException("הפעולה " + operation + " אינה מוכרת בחישוב ערך משפט.");
        }

        return resultVariable.getValue();
    }

    @Override
    protected String createBrackets(String data) {
        return data;
    }
}
