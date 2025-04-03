package ivrit.interpreter.Variables;

/**
 * The methods all variables with a numeric value should support.
 */
public interface NumericVariable extends Variable {
    //An array of all the operators Numeric Variables support:
    public static char[] NUMERIC_OPERATORS = { '+', '-', '*', '/' };

    /**
     * Adds the given value to the variable's value.
     * @param value - The value to be added (represented as a string).
     */
    public void add(String value);

    /**
     * Substracts the given value from the variable's value.
     * @param value - The value to substract (represented as a string).
     */
    public void substract(String value);

    /**
     * Multiplies the variable's value by the given value.
     * @param value - The value to multiply by (represented as a string).
     */
    public void multiply(String value);

    /**
     * Divides the variable's value by the given value.
     * @param value - The value to divide by (represented as a string).
     */
    public void divide(String value);

    /**
     * @return true IFF the variable's value is greater then the given value.
     */
    public BooleanVariable greaterThen(String value);

    /**
     * @return true IFF the variable's value is less then the given value.
     */
    public BooleanVariable lessThen(String value);
    //Static methods:

    /**
     * @return true IFF the given char is a numeric operator (as declared in the static field OPERATORS in this class).
     */
    public static boolean isNumericOperator(char ch) {
        for (char operator : NumericVariable.NUMERIC_OPERATORS) {
            if (ch == operator)
                return true;
        }

        return false;
    }

    /**
     * @return how many numeric operators are in the given string.
     */
    public static int countNumericOperators(String str) {
        String regex =  "\\+|-|\\*|/";

        String[] parts = str.split(regex);

        return parts.length - 1;
    }
}
