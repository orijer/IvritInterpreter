package ivrit.interpreter.Variables;

/**
 * An abstract type of variable.
 */
public abstract class AbstractVariable<T> implements Variable {
    protected T value;
    protected boolean isConst;

    /**
     * Construtor.
     */
    public AbstractVariable() {
        this.isConst = false;
    }

    /**
     * Construtor.
     * @param isConst - true IFF the variable cannot change it's value anymore (it's a constant)
     */
    public AbstractVariable(boolean isConst) {
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

    /**
     * @return true IFF this variable is a list of some type.
     */
    public boolean isList() {
        return false;
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
