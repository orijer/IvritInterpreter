package ivrit.interpreter.Evaluation;

/**
 * An evaluator that specializes in evaluating string expressions.
 */
public class StringEvaluator implements Evaluator{

    /**
     * Evaluates the value of a string.
     * @param originalData - The line we want to evaluate.
     * @return the value of the given string.
     * @throws IllegalArgumentException when there is no '+' before a literal string or a variable.
     * @throws UnsupportedOperationException when reading a '+' where it doesn't fit (example: two '+' in a row).
     * @throws IllegalArgumentException when the line ended while we were expecting another value.
     */
    public String evaluate(String originalData) {
        if (originalData.trim().startsWith("[")) {
            if (originalData.trim().endsWith("]")) { // just a list variable here
                return originalData;
            } else { // value at a specific index:
                String list = originalData.substring(0, originalData.indexOf("]")+1);
                int index = Integer.parseInt(originalData.substring(originalData.indexOf("]")+7).trim());
                return ListDereferencer.dereferenceStringsList(list, index);
            }
        }

        String line = originalData; //We want to keep the original data to be used when throwing an exception.
        StringBuilder result = new StringBuilder();
        result.append('"');

        boolean shouldAdd = true;
        int cutAt = 0;
        while (line.length() > 0) {
            if (line.charAt(0) == ' ') {
                //We skip all the empty spaces of the line:
                line = line.substring(1);

            } else if (line.charAt(0) == '"') {
                // We have a string literal:
                cutAt = line.indexOf('"', 1);

                if (!shouldAdd) {
                    throw new IllegalArgumentException("שגיאה: מחרוזת נכתבה ללא + מתאים לפניה בשורה '" + originalData + "'.");
                }

                result.append(line.substring(1, cutAt));
                shouldAdd = false;
                line = line.substring(cutAt + 1);

            } else if (line.charAt(0) == '+') {
                // We read the + operator:
                if (shouldAdd) {
                    throw new UnsupportedOperationException("שגיאה: נמצאה פעולת + במקום מידע להוספה בשורה '" + originalData + "'.");
                }

                shouldAdd = true;
                line = line.substring(1);

            } else {
                //We have a value (like a number or a boolean):
                cutAt = 1;
                while (cutAt < line.length() && line.charAt(cutAt) != ' ' && line.charAt(cutAt) != '+') {
                    cutAt++;
                }

                result.append(line.substring(0, cutAt));
                shouldAdd = false;
                line = line.substring(cutAt);
            }
        }

        if (shouldAdd) {
            //We were expecting another value before ending:
            throw new IllegalArgumentException("שגיאה: נמצאה פעולת + במקום מידע להוספה בשורה '" + originalData + "'.");
        }

        result.append('"');
        return result.toString();
    }
}
