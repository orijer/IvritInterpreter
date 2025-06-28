package ivrit.interpreter.Variables;

import java.util.HashMap;
import java.util.Map;

import ivrit.interpreter.UserIO.IvritIO;

/**
 * Stores the variables the user created and allows for fast lookup and updates for them. 
 */
public class Scope {
    //A map that connects between the name of the variable to its value:
    private Map<String, Variable> dataMap;

    /**
     * Constructor.
     */
    public Scope() {
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
     * @param name - The name of a variable we want to check whther it is a list or not.
     * @return true IFF there is a variable with the name name that is a list.
     */
    public boolean isList(String name) {
        return this.dataMap.get(name).isList();
    }

    /**
     * Creates a new variable.
     * @param variableName - The name of the new variable.
     * @param variableType - The type of the new variable.
     * @param variableValue - the value of the new variable.
     */
    public void createVariable(String variableName, String variableType, String variableValue, boolean isList,
            boolean isConstant) {
        Variable variable;
        if (this.dataMap.containsKey(variableValue)) {
            // if the value is the name of an existing variable, we first dereference it
            variableValue = this.dataMap.get(variableValue).getValue();
        }

        variable = VariablesFactory.createVariable(variableType, variableValue, isList, isConstant);

        this.dataMap.put(variableName, variable);
    }

    /**
     * Deletes the given variable from the program.
     * @param variableName - The name of the variable to delete.
     * @throws NullPointerException when variableName isn't the name of a variable. 
     */
    public void deleteVariable(String variableName) {
        if (!this.dataMap.containsKey(variableName))
            throw new NullPointerException("שגיאה: לא נמצא משתנה בשם '" + variableName + "'.");

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
            throw new NullPointerException("שגיאה: לא נמצא משתנה בשם '" + variableName + "'.");
        }

        if (variable.isConstant()) {
            throw new NumberFormatException("שגיאה: לא ניתן לשנות את הערך של קבוע '" + variableName + "'.");
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
     * Updates an item from a list at a specific index.
     * @param variableName - The name of the list variable.
     * @param index - The index we want to update. We start counting from 1 (so index 1 is the first index, unlike most languages).
     * @param newValue - The new value to use in that index of the list.
     * @throws NullPointerException if variableName is not a actually the name of a variable that is a list.
     */
    public void updateListVariable(String variableName, int index, String newValue) {
        Variable variable = dataMap.get(variableName);
        if (variable == null)
            throw new NullPointerException("שגיאה: לא קיים משתנה בשם '" + variableName + "'.");

        if (!variable.isList())
            throw new NullPointerException("שגיאה: אי אפשר לעדכן איבר במשתנה '" + variableName + "' שאינו רשימה.");

        ListVariable<?> lst = (ListVariable<?>) variable;
        lst.updateValueAtIndex(index, newValue);
    }

    /**
     * Adds a new item to the list at a specific index (the first index is always 1).
     * @param variableName - The name of the list variable.
     * @param index - The index we want the new item to be. "1" means we want it as the first element in the result. "end" means we want it to be the last.
     * @param value - The value to be inserted.
     * @throws NullPointerException if variableName is not a actually the name of a variable that is a list.
     */
    public void addToListVariable(String variableName, String index, String value) {
        Variable variable = dataMap.get(variableName);
        if (variable == null)
            throw new NullPointerException("שגיאה: לא קיים משתנה בשם '" + variableName + "'.");

        if (!variable.isList())
            throw new NullPointerException("שגיאה: אי אפשר להוסיף איבר למשתנה '" + variableName + "' שאינו רשימה.");

        ListVariable<?> lst = (ListVariable<?>) variable;
        lst.addValueAtIndex(index, value);
    }

    /**
     * Removes an item from the list at a specific index (the first index is always 1).
     * @param variableName - The name of the list variable.
     * @param index - The index we want the new item to be. "1" means we want it as the first element in the result. "end" means we want it to be the last.
     */
    public void removeFromListVariable(String variableName, String index) {
        Variable variable = dataMap.get(variableName);
        if (variable == null)
            throw new NullPointerException("שגיאה: לא קיים משתנה בשם '" + variableName + "'.");

        if (!variable.isList())
            throw new NullPointerException("שגיאה: אי אפשר להסיר איבר ממשתנה '" + variableName + "' שאינו רשימה.");

        ListVariable<?> lst = (ListVariable<?>) variable;
        lst.removeValueAtIndex(index);
    }

    /**
     * @param variableName - The name of the variable we wan the value of.
     * @return the value of the variable with the given name.
     * @throws NullPointerException when variableName isn't the name of a variable. 
     */
    public String getVariableValue(String variableName) {
        Variable variable = this.dataMap.get(variableName);
        if (variable == null)
            throw new NullPointerException("שגיאה: לא קיים משתנה בשם '" + variableName + "'.");

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
    public void printVariables(IvritIO io) {
        for (Map.Entry<String, Variable> entry : this.dataMap.entrySet()) {
            io.print("(" + entry.getKey() + " : " + entry.getValue().toString() + ")");
        }
    }
}
