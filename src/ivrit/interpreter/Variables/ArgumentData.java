package ivrit.interpreter.Variables;

/**
 * A class that contains all the information about a specific parameter of a function which we can later use when calling to that function.
 */
public class ArgumentData {
    // The name of the parameter.
    private String name;
    // The type of the parameter.
    private String type;
    // Whether or not the parameter is a list.
    private boolean isList;

    /**
     * Constructor.
     * @param name - The name of the parameter.
     * @param type - The type of the parameter.
     * @param isList - Whether or not the parameter is a list.
     */
    public ArgumentData(String name, String type, boolean isList) {
        this.name = name;
        this.type = type;
        this.isList = isList;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public boolean getIsList() {
        return this.isList;
    }
}
