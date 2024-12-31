package Evaluation;

import Variables.BooleanVariable;
import Variables.StringVariable;
import Variables.VariablesController;

/**
 * Handles evaluating a string based on its expected value type.
 */
public class EvaluationController {
    //Contains the variables of the program:
    private VariablesController variablesController;

    /**
     * Constructor.
     * @param variablesController - Contains the variables of the program.
     */
    public EvaluationController(VariablesController variablesController) {
        this.variablesController = variablesController;
    }

    /**
     * Evaluates a given data string.
     * @param data - The data to evaluate.
     * @return the evaluated value of the given string (still represented as a string).
     */
    public String evaluate(String data) {
        // We first switch all variables with their values, so now we only deal with the values themselves:
        VariableValueSwapper swapper = new VariableValueSwapper(this.variablesController);
        data = swapper.swap(data);

        Evaluator evaluator;
        
        //Find the correct evaluator type:
        if (BooleanVariable.containsBooleanExpression(data)) { 
            //Boolean evaluator:
            evaluator = new BooleanEvaluator();

        } else if (StringVariable.containsLiteralStrings(data)) { 
            //Strings evaluator (only one part of the line has to be a string for it to count here):
            evaluator = new StringEvaluator();

        } else {
            //Numerics evaluator:
            evaluator = new NumericEvaluator();
        }

        return evaluator.evaluate(data);
    }
}
