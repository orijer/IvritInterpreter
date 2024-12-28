package Variables;

/**
 * A variable that contains a string value.
 */
public class StringVariable extends AbstractVariable<String> {
    /**
     * Constructor.
     * @param value - The value of the variable.
     */
    public StringVariable(String value) {
        super(value);
    }

    /**
     * Constructor.
     * @param value - The value of the variable.
     * @param isConst - true IFF this variable is actually a const, meaning it cannot change it's value anymore.
     */
    public StringVariable(String value, boolean isConst) {
        super(value, isConst);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void updateValue(String newValue) {
        if (newValue.charAt(0) == '"' && newValue.charAt(newValue.length() - 1) == '"') {
            this.value = newValue;
        } else {
            throw new NumberFormatException(
                    "הערך " + newValue + " לא מתאים למשתנה מסוג משפט. ודאו שאכן השתמשתם במרכאות מסביב לקטע הרצוי.");
        }
    }

    @Override
    public String toString() {
        return this.value.substring(1, this.value.length() - 1);
    }

    //Static methods:

    /**
    * @param checkValue - The value to be checked.
    * @return true IFF checkValue is a valid string value in Ivrit.
    */
    public static boolean isStringValue(String checkValue) {
        return (checkValue.charAt(0) == '"' && checkValue.charAt(checkValue.length() - 1) == '"');
    }

    /**
    * @param str - The string to check.
    * @return true IFF the given string contains an Ivrit string in it (either by value: "משהו משהו", or by the name of a StringVariable).
    */
    public static boolean containsLiteralStrings(String str) {
        return (str.indexOf("\"") != str.lastIndexOf("\""));
    }
}
