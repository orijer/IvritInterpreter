package ivrit.interpreter.Evaluation;

import ivrit.interpreter.Variables.NumericVariable;
import ivrit.interpreter.Variables.VariablesFactory;

/**
 * An evaluator that specializes in evaluating numeric expressions.
 */
public class NumericEvaluator extends OrderedEvaluator {

    @Override
    protected String evaluateComponents(String operation, String data1, String data2) {
        if (operation.equals("במקום")) { // list dereferencing
            int index = Integer.parseInt(data1);
            return ListDereferencer.dereferenceNonStringsList(data2, index);
        }

        NumericVariable resultVariable = VariablesFactory.createNumericVariableByValue(data1);
        switch (operation) {
            case "+":
                resultVariable.add(data2);
                break;
            case "-":
                resultVariable.substract(data2);
                break;
            case "*":
                resultVariable.multiply(data2);
                break;
            case "/":
                resultVariable.divide(data2);
                break;   
            case "%":
                resultVariable.modulo(data2);
                break;             
            default:
                throw new UnsupportedOperationException("הפעולה " + operation + " אינה מוכרת בחישוב ערך שלם.");
        }

        return resultVariable.getValue();
    }

    @Override
    protected String createBrackets(String data) {
        int numOfOperators = NumericVariable.countNumericOperators(data);
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
