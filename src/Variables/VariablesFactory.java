package Variables;

/**
 * Handles creating the correct type of variable from a string.
 */
public class VariablesFactory {
    /**
     * @param type - The type of the variable
     * @param value - the value of the variable.
     * @return a new variable from the given type with the given value
     * @throws TypeNotPresentException when the type parameter is not recognized in the Ivrit language.
     */
    public static Variable createVariable(String type, String value, boolean isConstant) {
        switch (type) {
            case "טענה":
                return new BooleanVariable(value, isConstant);
            case "שלם":
                return new IntegerVariable(value, isConstant);
            case "עשרוני":
                return new FloatVariable(value, isConstant);
            case "משפט":
                return new StringVariable(value, isConstant);
            default:
                throw new TypeNotPresentException(type, new ClassNotFoundException());
        }
    }

    /**
     * Creates a numeric variable by using only the value.
     * @param value - The value the variable returned has.
     * @return - A numeric variable of the correct type, with the given value.
     * @throws ClassCastException when the given value cannot be the value of any NumericVariable.
     */
    public static NumericVariable createNumericVariableByValue(String value) {
        if (IntegerVariable.isIntegerValue(value)) {
            return new IntegerVariable(value);
        } 
        
        if (FloatVariable.isFloatValue(value)) {
            return new FloatVariable(value);
        }

        throw new ClassCastException("הערך " + value + "אינו מתאים לאף משתנה מספרי.");
    }
}
