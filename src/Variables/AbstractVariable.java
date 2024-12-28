package Variables;

/**
 * An abstract type of variable.
 */
public abstract class AbstractVariable<T> implements Variable {
    protected T value;
    protected boolean isConst;

    /**
     * Construtor.
     * @param value - The value of the variable.
     */
    public AbstractVariable(String value) {
        updateValue(value);
        this.isConst = false;
    }

    /**
     * Construtor.
     * @param value - The value of the variable.
     * @param isConst - true IFF the variable cannot change it's value anymore (it's a constant)
     */
    public AbstractVariable(String value, boolean isConst) {
        updateValue(value);
        this.isConst = isConst;
    }

    /**
     * Returns the simple name of the type of the data the variable contains.
     */
    public String getType() {
        return value.getClass().getSimpleName();
    }

    /**
     * Returns true IFF this is a constant, which means it cannot change it's value after initialization.
     */
    public boolean isConstant() {
        return this.isConst;
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
