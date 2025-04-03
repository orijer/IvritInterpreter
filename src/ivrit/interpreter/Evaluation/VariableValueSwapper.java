package ivrit.interpreter.Evaluation;

import java.io.IOException;
import java.io.UncheckedIOException;

import ivrit.interpreter.IvritExceptions.UnevenBracketsException;
import ivrit.interpreter.Variables.BooleanVariable;
import ivrit.interpreter.Variables.FloatVariable;
import ivrit.interpreter.Variables.IntegerVariable;
import ivrit.interpreter.Variables.NumericVariable;
import ivrit.interpreter.Variables.VariablesController;

/**
 * Handles switching the variables with their values for the evaluation.
 */
public class VariableValueSwapper {
    //Contains the variables of the program:
    private VariablesController variablesController;

    /**
     * Constructor.
     * @param variablesController - The object that handles the variables of the program.
     */
    public VariableValueSwapper(VariablesController variablesController) {
        this.variablesController = variablesController;
    }

    /**
     * Reads through a given data string and switches every occurence of a variable with its correct value.
     * @param data - The data to process.
     * @return a new string that is similar to the given string, but every occurence of a variable is switched with its value.
     */
    public String swap(String originalData) {
        String data = originalData; //We want to keep the original data to be used when throwing an exception.
        StringBuilder swappedLine = new StringBuilder();
        int endAt;

        while (data.length() > 0) {
            char firstChar = data.charAt(0);

            if (firstChar == '"') {
                data = copyStringLiteral(data, swappedLine, originalData);

            } else if (firstChar == ' ' || isBracket(firstChar) || NumericVariable.isNumericOperator(firstChar)) {
                //We are reading a space, a bracket, or an operator, just copy it:
                swappedLine.append(firstChar);
                data = data.substring(1);

            } else if ((endAt = BooleanVariable.startsWithBooleanOperator(data)) > 0) {
                //We are reading a boolean operator:
                swappedLine.append(data.substring(0, endAt));
                data = data.substring(endAt);

            } else if (data.startsWith("במקום ")) {
                swappedLine.append("במקום ");
                data = data.substring(6);

            } else {
                //We are reading a literal (non-string) value or a variable:
                endAt = dataEndAt(data);
                String literalValueOrVariable = data.substring(0, endAt);

                if (this.variablesController.isVariable(literalValueOrVariable)) {
                    //If it is a variable, switch it with its value:
                    swappedLine.append(this.variablesController.getVariableValue(literalValueOrVariable));

                } else {
                    //If it is literal data, try to copy it:
                    copyNonStringLiteral(literalValueOrVariable, swappedLine);
                }

                data = data.substring(endAt);
            }
        }

        return swappedLine.toString();
    }

    /**
     * If given a valid string literal, add its value to the given StringBuilder, 
     * and return the data without the string literal that was found.
     * @param data - The data that should start with a string literal.
     * @param swappedLine - The StringBuilder we build the evaluated result in.
     * @return a substring of the given data string that starts after the string literal that was found.
     */
    private String copyStringLiteral(String data, StringBuilder swappedLine, String originalData) {
        int endAt = data.indexOf('"', 1);

        if (endAt == -1) {
            throw new UnevenBracketsException(originalData);
        }

        swappedLine.append(data.substring(0, endAt + 1));
        return data.substring(endAt + 1);
    }

    /**
     * Copies a non string literal to the given StringBuilder, if it's valid (an actual value of a variable type).
     * @param literalData - The data that should be a literal of a certain variable type.
     * @param swappedLine - The string Builder we build the evaluaed result in.
     */
    private void copyNonStringLiteral(String literalData, StringBuilder swappedLine) {
        if (BooleanVariable.isBooleanValue(literalData)
                || IntegerVariable.isIntegerValue(literalData)
                || FloatVariable.isFloatValue(literalData)) {
            swappedLine.append(literalData);

        } else {
            throw new UncheckedIOException("לא ניתן להבין את משמעות המילה " + literalData, new IOException());
        }
    }

    /**
     * @param data - The data to process.
     * @return the index of the first char of the second piece of data in he given data string (operator or space or variable, etc)
     */
    private int dataEndAt(String data) {
        int endAt = 1;
        while (endAt < data.length() && data.charAt(endAt) != '"' && data.charAt(endAt) != ' '
                && !isBracket(data.charAt(endAt)) && !NumericVariable.isNumericOperator(data.charAt(endAt))) {
            endAt++;
        }

        return endAt;
    }

    /**
     * @param ch - The char to check.
     * @return true IFF the given char is a bracket.
     */
    private boolean isBracket(char ch) {
        return (ch == '(' || ch == ')');
    }

}
