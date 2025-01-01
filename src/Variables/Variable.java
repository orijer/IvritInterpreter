package Variables;

/**
 * Represents a variable created by the user in the .Ivrit file.
 */
public interface Variable {
    /**
     * @return the value of the variable.
     */
    public String getValue();
    
    /**
     * Updates the value of the variable with a new value.
     * @param newValue - The new value of the variable.
     * @throws NumberFormatException when newValue can't be casted directly to the correct type.
     */
    public void updateValue(String newValue);

    /**
     * Returns a string containing the type of the variable.
     */
    public String getType();

    /**
     * Returns true IFF this is a constant, which means it cannot change it's value after initialization.
     */
    public boolean isConstant();

    /**
     * @return true IFF this variable is a list of some type.
     */
    public boolean isList();

    /**
     * Returns a new Variable object of the same the as the object this was called in.
     */
    public Variable createNewVariableWithSameType(String value);
}
