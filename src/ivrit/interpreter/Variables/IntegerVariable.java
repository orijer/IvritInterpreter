package ivrit.interpreter.Variables;

/**
 * A variable that contains an integer value (a whole number).
 */
public class IntegerVariable extends AbstractVariable<Integer> implements NumericVariable { 
    /**
     * Constructor.
     * @param value - The value of the variable.
     */
    public IntegerVariable(String value) {
        super();
        updateValue(value);
    }

    /**
     * Constructor.
     * @param value - The value of the variable.
     * @param isConst - true IFF this variable is actually a const, meaning it cannot change it's value anymore.
     */
    public IntegerVariable(String value, boolean isConst) {
        super(isConst);
        updateValue(value);
    }

    @Override
    public String getValue() {
        return Integer.toString(this.value);
    }

    @Override
    public void updateValue(String newValue) {
        try {
            this.value = Integer.parseInt(newValue);
        } catch (NumberFormatException exception) {
            throw new NumberFormatException("שגיאה: הערך " + newValue + " לא מתאים למשתנה מסוג שלם.");
        }
    }

    @Override
    public Variable createNewVariableWithSameType(String value) {
        return new IntegerVariable(value);
    }

    /**
     * @param checkValue - The value to be checked.
     * @return true IFF checkValue is a valid integer value in Ivrit.
     */
    public static boolean isIntegerValue(String checkValue) {
        try {
            Integer.parseInt(checkValue);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    //NumericVariable methods:

    @Override
    public void add(String value) {
        if (IntegerVariable.isIntegerValue(value)) {
            this.value += Integer.parseInt(value);

        } else if (FloatVariable.isFloatValue(value)) {
            this.value += (int) Float.parseFloat(value);

        } else
            throw new ClassCastException("שגיאה: לא ניתן לחבר למשתנה מסוג שלם את הערך " + value);
    }

    @Override
    public void substract(String value) {
        if (IntegerVariable.isIntegerValue(value)) {
            this.value -= Integer.parseInt(value);

        } else if (FloatVariable.isFloatValue(value)) {
            this.value -= (int) Float.parseFloat(value);

        } else
            throw new ClassCastException("שגיאה: לא ניתן לחסר ממשתנה מסוג שלם את הערך " + value);
    }

    @Override
    public void multiply(String value) {
        if (IntegerVariable.isIntegerValue(value)) {
            this.value *= Integer.parseInt(value);

        } else if (FloatVariable.isFloatValue(value)) {
            this.value *= (int) Float.parseFloat(value);

        } else
            throw new ClassCastException("שגיאה: לא ניתן להכפיל משתנה מסוג שלם בערך " + value);
    }

    @Override
    public void divide(String value) {
        if (IntegerVariable.isIntegerValue(value)) {
            this.value /= Integer.parseInt(value);

        } else if (FloatVariable.isFloatValue(value)) {
            this.value /= (int) Float.parseFloat(value);

        } else
            throw new ClassCastException("שגיאה: לא ניתן לחלק משתנה מסוג שלם בערך " + value);
    }

    @Override
    public void modulo(String value) {
        if (IntegerVariable.isIntegerValue(value)) {
            this.value %= Integer.parseInt(value);

        } else if (FloatVariable.isFloatValue(value)) {
            this.value %= (int) Float.parseFloat(value);

        } else
            throw new ClassCastException("שגיאה: לא ניתן לחשב שארית עבור משתנה מסוג שלם והערך " + value);
    }

    @Override
    public BooleanVariable greaterThen(String value) {
        String result = "שקר";

        if (IntegerVariable.isIntegerValue(value)) {
            if (this.value > Integer.parseInt(value))
                result = "אמת";

        } else if (FloatVariable.isFloatValue(value)) {
            if (this.value > Float.parseFloat(value))
                result = "אמת";

        } else
            throw new ClassCastException("שגיאה: לא ניתן להשוות משתנה מסוג שלם לערך " + value);

        return new BooleanVariable(result);
    }

    @Override
    public BooleanVariable lessThen(String value) {
        String result = "שקר";

        if (IntegerVariable.isIntegerValue(value)) {
            if (this.value < Integer.parseInt(value))
                result = "אמת";

        } else if (FloatVariable.isFloatValue(value)) {
            if (this.value < Float.parseFloat(value))
                result = "אמת";

        } else
            throw new ClassCastException("שגיאה: לא ניתן להשוות משתנה מסוג שלם לערך " + value);

        return new BooleanVariable(result);
    }
}
