package Variables;

/**
 * An abstract type of variable.
 */
public abstract class AbstractVariable<T> implements Variable {
    protected T value;

    /**
     * Construtor.
     * @param value - The value of the variable.
     */
    public AbstractVariable(String value) {
        updateValue(value);
    }

    /**
     * Returns the simple name of the type of the data the variable contains.
     */
    public String getType() {
        return value.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return getValue();
    }

    /**
     * @return true IFF the given value is a valid value for at least one variable type.
     */
    public static boolean isLiteralValue(String value) {
        return (BooleanVariable.isBooleanValue(value) || IntegerVariable.isIntegerValue(value)
                || FloatVariable.isFloatValue(value) || StringVariable.isStringValue(value));
    }
}
