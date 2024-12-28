package Variables;

/**
 * A variable that contains a boolean value (true or false).
 */
public class BooleanVariable extends AbstractVariable<Boolean> {
    //An array of all the operators boolean values support:
    public static String[] BOOLEAN_OPERATORS = { "שווה", "לא-שווה", "וגם", "או", ">", "<" };

    /**
     * Constructor.
     * @param value - The value of the variable.
     */
    public BooleanVariable(String value) {
        super(value);
    }

    /**
     * Constructor.
     * @param value - The value of the variable.
     * @param isConst - true IFF this variable is actually a const, meaning it cannot change it's value anymore.
     */
    public BooleanVariable(String value, boolean isConst) {
        super(value, isConst);
    }

    @Override
    public String getValue() {
        if (value) {
            return "אמת";
        }

        return "שקר";
    }

    @Override
    public void updateValue(String newValue) {
        if (newValue.equals("אמת")) {
            this.value = true;
        } else if (newValue.equals("שקר")) {
            this.value = false;
        } else {
            throw new NumberFormatException("הערך " + newValue + " לא מתאים למשתנה מסוג טענה.");
        }
    }

    /**
     * @param checkValue - The value to be checked
     * @return true IFF checkValue is a valid boolean value in Ivrit.
     */
    public static boolean isBooleanValue(String checkValue) {
        return checkValue.equals("אמת") || checkValue.equals("שקר");
    }

    //Static methods:

    /**
    * @return true IFF the given data contains a boolean expression (that isn't in quotes, ""וגם"" doesn't count)
    */
    public static boolean containsBooleanExpression(String data) {
        boolean inQuotes = false;

        for (int index = 0; index < data.length(); index++) {
            if (data.charAt(index) == '"')
                inQuotes = !inQuotes;
            else if (!inQuotes) {
                for (String operator : BOOLEAN_OPERATORS) {
                    //Check if the data contains an operator here that isn't in quotes:
                    if (data.startsWith(operator, index))
                        return true;
                }
            }
        }

        return false;
    }

    /**
     * @return 0 if the given string doesn't start with a boolean operatorm or the operator length (how many characters it has) otherwise.
     */
    public static int startsWithBooleanOperator(String data) {
        for (String operator : BOOLEAN_OPERATORS) {
            if (data.startsWith(operator))
                return operator.length();
        }

        return 0;
    }
}
