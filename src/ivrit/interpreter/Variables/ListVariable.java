package ivrit.interpreter.Variables;

import java.util.ArrayList;
import java.util.List;

/**
 * A variable that contains a list of variables of a certain type.
 */
public class ListVariable<T extends Variable> extends AbstractVariable<List<T>> {
    private T classVar; // we need to keep it in order to add new items of te correct type to the list.
    /**
     * Constructor.
     * @param value - The value of the variable.
     */
    public ListVariable(String value, T classVar) {
        super();
        this.classVar = classVar;
        updateValue(value);
    }

    /**
     * Constructor.
     * @param value - The value of the variable.
     * @param isConst - true IFF this variable is actually a const, meaning it cannot change it's value anymore.
     */
    public ListVariable(String value, boolean isConst, T classVar) {
        super(isConst);
        this.classVar = classVar;
        updateValue(value);
    }

    @Override
    public String getValue() {
        String res = "";
        for (int i = 0; i < this.value.size()-1; i++) {
            res+= this.value.get(i).toString() + ", ";
        }
        res+= this.value.get(this.value.size()-1);
        return "[" + res + "]";
    }

    @Override
    public void updateValue(String newValue) {
        try {
            if (!newValue.startsWith("[") || !newValue.endsWith("]"))
                throw new NumberFormatException("שגיאה: הערך " + newValue + " לא מתאים למשתנה שהוא רשימה כי הוא לא נראה כמו: [ביטויים שמופרדים בפסיקים].");

            newValue = newValue.substring(1, newValue.length()-1).trim(); //remove the brackets of the start and end.
            this.value = new ArrayList<T>();
            
            if (this.classVar instanceof StringVariable) { // we need to split strings more carefully...
                int start = 0;
                int i = 1;
                while (i < newValue.length()) {
                    if (newValue.charAt(i) == '\"') {
                        @SuppressWarnings("unchecked") // createNewVariableWithSameType on an object of type T, returns an object of type T!
                        T item = (T) classVar.createNewVariableWithSameType(newValue.substring(start, i + 1));
                        this.value.add(item);

                        i++;
                        while (i < newValue.length() && (newValue.charAt(i) == ' ' || newValue.charAt(i) == ','))
                            i++;

                        start = i;
                        i++;
                    } else {
                        i++;
                    }
                }

            } else {
                String[] values = newValue.split(",");
                for (String value : values) {
                    @SuppressWarnings("unchecked") // createNewVariableWithSameType on an object of type T, returns an object of type T!
                    T item = (T) this.classVar.createNewVariableWithSameType(value.trim());
                    this.value.add(item);
                }
            }
        } catch (NumberFormatException exception) {
            throw new NumberFormatException("שגיאה: הערך " + newValue + " לא מתאים למשתנה שהוא רשימה.");
        }
    }

    public T getValueAtIndex(int index) {
        if (index > this.value.size())
            throw new IndexOutOfBoundsException("שגיאה: המיקום " + index + " הוא גדול מידי עבור רשימה בגודל " + this.value.size() + ".");

        return this.value.get(index-1); // since in Ivrit we start indexing lists from 1.
    }

    public void updateValueAtIndex(int index, String newValue) {
        if (index < 1) {
            throw new IndexOutOfBoundsException("שגיאה: המיקום " + index + " הוא קטן מידי עבור רשימה (צריך לפחות 1).");
        }

        if (index > this.value.size()) {
            throw new IndexOutOfBoundsException("שגיאה: המיקום " + index + " הוא גדול מידי עבור רשימה בגודל " + this.value.size() + ".");
        }

        this.value.get(index-1).updateValue(newValue);
    }

    public void addValueAtIndex(String stringIndex, String value) {
        int index = 0;
        if (stringIndex.equals("end")) {
            index = this.value.size()+1;
        } else {
            index = Integer.parseInt(stringIndex);
        }

        if (index < 1) {
            throw new IndexOutOfBoundsException("שגיאה: המיקום " + index + " הוא קטן מידי עבור רשימה (צריך לפחות 1).");
        }

        if (index > this.value.size() + 1) {
            throw new IndexOutOfBoundsException("שגיאה: המיקום " + index + " הוא גדול מידי עבור רשימה בגודל " + this.value.size() + ".");
        }

        @SuppressWarnings("unchecked") // createNewVariableWithSameType on an object of type T, returns an object of type T!
        T item = (T) classVar.createNewVariableWithSameType(value.trim());
        this.value.add(index-1, item);
    }

    public void removeValueAtIndex(String stringIndex) {
        int index = 0;
        if (stringIndex.equals("end")) {
            index = this.value.size();
        } else {
            index = Integer.parseInt(stringIndex);
        }

        if (index < 1) {
            throw new IndexOutOfBoundsException("שגיאה: המיקום " + index + " הוא קטן מידי עבור רשימה (צריך לפחות 1).");
        }

        if (index > this.value.size() + 1) {
            throw new IndexOutOfBoundsException(
                    "שגיאה: המיקום " + index + " הוא גדול מידי עבור רשימה בגודל " + this.value.size() + ".");
        }

        this.value.remove(index-1);
    }

    @Override
    public boolean isList() {
        return true;
    }

    /**
     * @param checkValue - The value to be checked.
     * @return true IFF checkValue is a valid float value in Ivrit.
     */
    public static boolean isListValue(String checkValue) {
        return checkValue.startsWith("[") && checkValue.endsWith("]"); //we might need to swap the order....
    }

    @Override
    public Variable createNewVariableWithSameType(String value){
        return null;
    }
}
