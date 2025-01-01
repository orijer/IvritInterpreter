package Evaluation;

import IvritExceptions.UnevenBracketsException;

/**
 * Each evaluator should specialize in evaluating a specific type of data string.
 */
public interface Evaluator {
    /**
     * @return evaluates the given string and returns it.
     */
    public String evaluate(String data);

    /**
    * @param str - The string to check.
    * @return true IFF the given string contains both type of brackets.
    * @throws UnevenBracketsException when the given string contains exactly one side of brackets and not both.
    */
    public static boolean containsBracket(String str) {
        boolean containsBracket1 = str.contains(")");
        boolean containsBracket2 = str.contains("(");

        if (containsBracket1 && containsBracket2)
            return true;

        //If only one of them is true, then we have uneven amount of brackets:
        if (containsBracket1 || containsBracket2)
            throw new UnevenBracketsException(str);

        //No brackets at all:
        return false;
    }
}
