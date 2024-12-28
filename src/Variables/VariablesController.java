package Variables;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the variables the user created and allows for fast lookup and updates for them. 
 */
public class VariablesController {
    //A map that connects between the name of the variable to its value:
    private Map<String, Variable> dataMap;

    /**
     * Constructor.
     */
    public VariablesController() {
        this.dataMap = new HashMap<>();
    }

    /**
     * @param name - The name of the variable to check.
     * @return true IFF there is already a variable with the given name stored.
     */
    public boolean isVariable(String name) {
        return this.dataMap.containsKey(name);
    }

    /**
     * Creates a new variable.
     * @param variableName - The name of the new variable.
     * @param variableType - The type of the new variable.
     * @param variableValue - the value of the new variable.
     */
    public void createVariable(String variableName, String variableType, String variableValue, boolean isConstant) {
        Variable variable;
        if (this.dataMap.containsKey(variableValue)) {
            //If we are assigning a variable to this (we only copy by value):
            variable = VariablesFactory.createVariable(variableType, this.dataMap.get(variableValue).getValue(), isConstant);

        } else {
            //We are creating a variable from the value:
            variable = VariablesFactory.createVariable(variableType, variableValue, isConstant);

        }

        this.dataMap.put(variableName, variable);
    }

    /**
     * Deletes the given variable from the program.
     * @param variableName - The name of the variable to delete.
     * @throws NullPointerException when variableName isn't the name of a variable. 
     */
    public void deleteVariable(String variableName) {
        if (!this.dataMap.containsKey(variableName))
            throw new NullPointerException("לא נמצא משתנה בשם: " + variableName);

        this.dataMap.remove(variableName);
    }

    /**
     * Updates the value of an existing variable.
     * @param variableName - The name of the variable to be updated.
     * @param newValue - The new value.
     * @throws NullPointerException when variableName isn't the name of a variable. 
     * @throws NumberFormatException when trying to update the value of a constant.
     */
    public void updateVariable(String variableName, String newValue) {
        Variable variable = this.dataMap.get(variableName);
        if (variable == null) {
            throw new NullPointerException("לא נמצא משתנה בשם: " + variableName);
        }

        if (variable.isConstant()) {
            throw new NumberFormatException("לא ניתן לשנות את הערך של קבוע");
        }

        if (this.dataMap.containsKey(newValue)) {
            //If we are assigning a variable to this (we only copy by value):
            variable.updateValue(this.dataMap.get(newValue).getValue());
        } else {
            //We are updating from the value:
            variable.updateValue(newValue);

        }
    }

    /**
     * @param variableName - The name of the variable we wan the value of.
     * @return the value of the variable with the given name.
     * @throws NullPointerException when variableName isn't the name of a variable. 
     */
    public String getVariableValue(String variableName) {
        Variable variable = this.dataMap.get(variableName);
        if (variable == null) {
            throw new NullPointerException("לא נמצא משתנה בשם: " + variableName);
        }

        return variable.getValue();
    }

    /**
     * Clears all the variables created.
     */
    public void clear() {
        this.dataMap.clear();
    }

    /**
     * Prints all the variables in the format (variableName : variableValue).
     */
    public void printVariables() {
        for (Map.Entry<String, Variable> entry : this.dataMap.entrySet()) {
            System.out.println("(" + entry.getKey() + " : " + entry.getValue().toString() + ")");
        }
    }
}
