package ivrit.interpreter.Evaluation;

import ivrit.interpreter.Variables.BooleanVariable;
import ivrit.interpreter.Variables.NumericVariable;
import ivrit.interpreter.Variables.VariablesFactory;

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
        int numOfOperators = BooleanVariable.countBooleanOperators(data);
        if (numOfOperators == 0) {
            //No operations = no brackets needed:
            return data;

        } else if (numOfOperators == 1) {
            //There is only one operation = just add brackets to it if needed:
            if (data.charAt(0) == '(' && data.charAt(data.length() - 1) == ')')
                return data;

            return '(' + data + ')';
        }

        //TODO: Implement adding more than 1 pair of brackets later:
        return data;
    }
}
