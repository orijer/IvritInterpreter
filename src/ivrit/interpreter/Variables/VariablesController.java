package ivrit.interpreter.Variables;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ivrit.interpreter.UserIO.IvritIO;

/**
 * Stores the scopes of the program, which contain the mappings between the variables to their values.
 * This allows us declare the same variable twice in different scopes, and treat them as two different variables.
 * Very useful in functions (for example, in recursions).
 */
public class VariablesController {
    //A list that contains all the scopes of the program.
    private List<Scope> scopes;
    // A mapping between function names and the data about their parameters.
    Map<String,List<ArgumentData>> functionDefinitions;

    /**
     * Constructor.
     */
    public VariablesController(Map<String,List<ArgumentData>> functionDefinitions) {
        this.scopes = new LinkedList<>();
        this.scopes.add(new Scope()); // The global scope.
        this.functionDefinitions = functionDefinitions;
    }

    /**
     * @param name - The name of the variable to check.
     * @return true IFF there is already a variable with the given name stored in some scope.
     */
    public boolean isVariable(String name) {
        for (Scope scope : this.scopes) {
            if (scope.isVariable(name))
                return true;
        }

        return false;
    }

    /**
     * @param name - The name of a variable we want to check whether it is a list or not.
     * @return true IFF there is a variable with the name name that is a list, and also it is the most recent variable with that name 
     * (meaning it isn't hidden by a more recent non-list variable with the same name).
     */
    public boolean isList(String name) {
        for (int i = this.scopes.size()-1; i>=0; i--) {
            Scope scope = this.scopes.get(i);
            if (scope.isVariable(name)) {
                return scope.isList(name);
            }
        }

        return false;
    }

    /**
     * Creates a new variable in the most recent scope.
     * @param variableName - The name of the new variable.
     * @param variableType - The type of the new variable.
     * @param variableValue - the value of the new variable.
     */
    public void createVariable(String variableName, String variableType, String variableValue, boolean isList, boolean isConstant) {
        this.scopes.get(this.scopes.size()-1).createVariable(variableName, variableType, variableValue, isList, isConstant);
    }

    /**
     * Deletes the given variable from the most recent scope it exists in.
     * @param variableName - The name of the variable to delete.
     * @throws NullPointerException when variableName isn't the name of a variable. 
     */
    public void deleteVariable(String variableName) {
        for (int i = this.scopes.size() - 1; i >= 0; i--) {
            Scope scope = this.scopes.get(i);
            try {
                scope.deleteVariable(variableName);
                return; // return afer a successful deletion.
            } catch (NullPointerException e) {
                // Do nothing. There is no such variable in that scope.
            }
        }

        throw new NullPointerException("שגיאה: לא נמצא משתנה בשם '" + variableName + "'.");
    }

    /**
     * Updates the value of an existing variable in the most recent scope it exists in.
     * @param variableName - The name of the variable to be updated.
     * @param newValue - The new value.
     * @throws NullPointerException when variableName isn't the name of a variable. 
     * @throws NumberFormatException when trying to update the value of a constant.
     */
    public void updateVariable(String variableName, String newValue) {
        for (int i = this.scopes.size() - 1; i >= 0; i--) {
            Scope scope = this.scopes.get(i);
            try {
                scope.updateVariable(variableName, newValue);
                return; // return afer a successful update.
            } catch (NumberFormatException e) {
                throw e; // thrown if we are trying to update a constant.
            } catch (NullPointerException e) {
                // Do nothing. There is no such variable in that scope.
            }
        }

        throw new NullPointerException("שגיאה: לא נמצא משתנה בשם '" + variableName + "'."); // incase no such variable is found at all.
    }
    
    /**
     * Updates an item from a list at a specific index.
     * @param variableName - The name of the list variable.
     * @param index - The index we want to update. We start counting from 1 (so index 1 is the first index, unlike most languages).
     * @param newValue - The new value to use in that index of the list.
     * @throws NullPointerException if variableName is not a actually the name of a variable that is a list.
     * @throws NumberFormatException if we are trying to update a non-list as a list.
     */
    public void updateListVariable(String variableName, int index, String newValue) {
        for (int i = this.scopes.size() - 1; i >= 0; i--) {
            Scope scope = this.scopes.get(i);
            try {
                scope.updateListVariable(variableName, index, newValue);
                return; // return afer a successful update.
            } catch (NumberFormatException e) {
                throw e;
            } catch (NullPointerException e) {
                // Do nothing. There is no such variable in that scope.
            }
        }

        throw new NullPointerException("שגיאה: לא נמצא משתנה בשם '" + variableName + "'."); // incase no such variable is found at all.
    }

    /**
     * Adds a new item to the list at a specific index (the first index is always 1).
     * @param variableName - The name of the list variable.
     * @param index - The index we want the new item to be. "1" means we want it as the first element in the result. "end" means we want it to be the last.
     * @param value - The value to be inserted.
     * @throws NullPointerException if variableName is not a actually the name of a variable that is a list.
     * @throws NumberFormatException if we are trying to update a non-list as a list.
     */
    public void addToListVariable(String variableName, String index, String value) {
        for (int i = this.scopes.size() - 1; i >= 0; i--) {
            Scope scope = this.scopes.get(i);
            try {
                scope.addToListVariable(variableName, index, value);
                return; // return afer a successful update.
            } catch (NumberFormatException e) {
                throw e;
            } catch (NullPointerException e) {
                // Do nothing. There is no such variable in that scope.
            }
        }

        throw new NullPointerException("שגיאה: לא נמצא משתנה בשם '" + variableName + "'."); // incase no such variable is found at all.
    }

    /**
     * @param variableName - The name of the variable we wan the value of.
     * @return the value of the variable with the given name.
     * @throws NullPointerException when variableName isn't the name of a variable. 
     */
    public String getVariableValue(String variableName) {
        for (int i = this.scopes.size() - 1; i >= 0; i--) {
            Scope scope = this.scopes.get(i);
            try {
                String val = scope.getVariableValue(variableName);
                return val;
            } catch (NullPointerException e) {
                // Do nothing. There is no such variable in that scope. Maybe it's in a previous scope.
            }
        }

        throw new NullPointerException("שגיאה: לא קיים משתנה בשם '" + variableName + "'.");
    }

    /**
     * Clears all the variables created in all scopes.
     */
    public void clear() {
        for (Scope scope : this.scopes) {
            scope.clear();
        }
    }

    /**
     * Prints all the variables in the format (variableName : variableValue).
     */
    public void printVariables(IvritIO io) {
        for (Scope scope : this.scopes) {
            scope.printVariables(io);
        }
    }

    /**
     * Creates a new empty scope for the program.
     */
    public void createScope() {
        this.scopes.add(new Scope());
    }

    /**
     * Creates a new scope for the program which contains some values (useful for passing arguments to function scopes).
     */
    public void createScope(String functionName, String[] args) {
        Scope newScope = new Scope();
        List<ArgumentData> expectedArgs = this.functionDefinitions.get(functionName);
        if (expectedArgs == null)
            throw new IllegalArgumentException("שגיאה: לא נמצאה פונקציה בשם '" + functionName + "'.");

        if (expectedArgs.size() != args.length)
            throw new IllegalArgumentException("שגיאה: בקריאה לפונקציה '" + functionName + "' נשלחו " + args.length + "ארגומנטים, אך ציפינו לקבל " + expectedArgs.size());

        for (int i = 0; i < args.length; i++) {
            ArgumentData currData = expectedArgs.get(i);
            try {
                String value = getVariableValue(args[i]);
                newScope.createVariable(currData.getName(), currData.getType(), value, currData.getIsList(), false);
            } catch (NullPointerException e) {
                newScope.createVariable(currData.getName(), currData.getType(), args[i], currData.getIsList(), false);
            }
        }

        this.scopes.add(newScope);
    }

    /**
     * Clears the most recent scope and removes it from the program.
     */
    public void popScope() {
        int last = this.scopes.size() - 1;
        this.scopes.get(last).clear();
        this.scopes.remove(last);
    }
}
