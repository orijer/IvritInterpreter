package Evaluation;

import java.util.LinkedList;

import IvritExceptions.InterpreterExceptions.EvaluatorExceptions.UnevenBracketsException;
import IvritExceptions.InterpreterExceptions.EvaluatorExceptions.UnknownEvaluationFormatException;

/**
 * An abstract class for all the evaluators that first create brackets in the data, 
 * and then simplify each bracket individually until there are non left.
 */
public abstract class OrderedEvaluator implements Evaluator {
    @Override
    public String evaluate(String data) {
        data = createBrackets(data);

        while (Evaluator.containsBracket(data)) {
            data = simplifyBrackets(data);
        }

        return data;
    }

    /**
    * Simplifies the most right pair of brackets and return the new simplified string.
    * @param data - The line containing a bracket to simplify.
    * @return a new simplified string after the most right brackets were simplified.
    * @throws UnevenBracketsException when the given string contains exactly one side of brackets and not both.
    * @throws UnknownEvaluationFormatException when the bracketed data isn't in the form of: data, operation, data.
    */
    protected String simplifyBrackets(String originalData) {
        String data = originalData; //We want to keep the original data to be used when throwing an exception.
        StringBuilder result = new StringBuilder(data);

        //Choose the brackets to simplify:
        int start = data.lastIndexOf('(');
        int end = data.indexOf(')', start);

        //Check both brackets exist:
        if (start == -1 || end == -1) {
            throw new UnevenBracketsException(originalData);
        }

        String simplificationResult;

        //Split the brackets to data, operation, data:
        String segment = data.substring(start + 1, end);
        String[] components = segmentSplit(segment);

        if (components.length == 1) {
            //Brackets that just contain one value should return the value without the brackets:
            simplificationResult = segment;

        } else if (components.length == 3) {
            //Standart data1 operation data2, we can just evaluate it:
            simplificationResult = evaluateComponents(components[1], components[2], components[0]);

        } else {
            //We dont know how to evaluate this... probably a mistake from the user:
            throw new UnknownEvaluationFormatException(components.length, segment, originalData);
        }

        result.replace(start, end + 1, simplificationResult);
        return result.toString();
    }

    /**
     * Splits the given string to its components 
     * (multiple words in the same string literal will remain in the same component)
     * @param segment - The segment to split.
     * @return an array of Strings that contains the components of the given string in the same order as the given string.
     * @throws Exception
     */
    private String[] segmentSplit(String segment) {
        LinkedList<String> componentList = new LinkedList<String>();

        if (segment.charAt(0) == '[') { // then this is an operation on a list:
            int listEndsAt = segment.indexOf(']');
            if (listEndsAt == -1)
                throw new UnevenBracketsException(segment);

            if (segment.indexOf('[', listEndsAt) != -1) //this is the only list here
                throw new RuntimeException("");

            componentList.add(segment.substring(0, listEndsAt+1).trim());
            segment = segment.substring(listEndsAt+1).trim();
            int operationEndsAt = segment.indexOf(' ');
            if (operationEndsAt == -1)
                throw new RuntimeException();
            componentList.add(segment.substring(0, operationEndsAt).trim());
            componentList.add(segment.substring(operationEndsAt+1).trim());
            return componentList.toArray(new String[0]);
        }

        boolean componentStarted = false;
        boolean inQuote = false;

        int start = 0;
        int index = 0;
        while (index < segment.length()) {
            if (segment.charAt(index) == '"') {
                //Handle strings:
                if (componentStarted) {
                    //This is the end of a string, add all of it to the linked list:
                    componentStarted = false;
                    inQuote = false;
                    componentList.addLast(segment.substring(start, index + 1));
                    start = index + 1;
                } else {
                    //This is the start of a string:
                    componentStarted = true;
                    inQuote = true;
                    start = index;
                }
                
            } else if (segment.charAt(index) == ' ' && !inQuote) {
                if (componentStarted) {
                    //This is the end of a component:
                    componentStarted = false;
                    componentList.addLast(segment.substring(start, index));
                    start = index + 1;
                } else {
                    //Just an empty space we should ignore:
                    start = index + 1;
                }

            } else if (index == (segment.length() - 1)) {
                //This is the end of the segment, add the last component if needed:
                componentList.addLast(segment.substring(start, index + 1));

            } else {
                //Make sure we remember we are in a component:
                componentStarted = true;
            }

            index++;
        }

        return componentList.toArray(new String[0]);
    }

    /**
     * Evaluates the result of data1 operation data2
     * @param operation - The operation we want to do.
     * @param data1 - The data on the left of the operation.
     * @param data2 - The data on the right of the operation.
     * @return the value of data1 operation data2.
     */
    protected abstract String evaluateComponents(String operation, String data1, String data2);

    /**
     * @param data - The line to give brackets to.
     * @return a copy of the given line, that includes brackets wherever needed.
     */
    protected abstract String createBrackets(String data);

}
