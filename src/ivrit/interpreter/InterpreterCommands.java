package ivrit.interpreter;

public enum InterpreterCommands {
    PRINT("הדפס"),
    VARIABLE("משתנה"),
    CONSTANT("קבוע"),
    DELETE("מחק"),
    IF("אם"),
    JUMP("קפוץ-ל"),
    CALL("הפעל"),
    RETURN("תחזיר", "תחזור"),
    INPUT("קלוט-ל"),
    ADD("הוסף"),
    EXIT("צא");

    private final String[] literals;

    InterpreterCommands(String... literals) { // this allows for synonyms, like in the case of RETURN.
        this.literals = literals;
    }

    public String[] getLiterals() {
        return literals;
    }

    public boolean matches(String action) {
        for (String literal : literals)
            if (literal.equals(action)) 
                return true;

        return false;
    }

    public static InterpreterCommands fromString(String action) {
        for (InterpreterCommands command : values()) 
            if (command.matches(action)) 
                return command;
            
        return null;
    }
}
