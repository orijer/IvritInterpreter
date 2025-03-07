import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Stack;

import IvritStreams.RestartableReader;

/**
 * The object that handles jumping a RestartableReader to a jump flag.
 */
public class Jumper {
    // Contains a map that connects the jump flags to how many lines need to be skipped in order to get to the correct line of code.
    private Map<String, Integer> jumpMap;
    // A stack that contains the links we are expecting to see in a LIFO order (Used in the if mechanism).
    private Stack<JumpFlagLinker> jumpFlagsLinksStack;
    // Maps between the function name and the line it's code starts at.
    private Map<String, Integer> funcMap;
    // A stack that contains the function return lines (the top item is the number of the line a return should go back to).
    private Stack<Integer> returnLinesStack;
    // The active reader that needs to jump:
    private RestartableReader reader;

    /**
     * Constructor.
     * @param jumpMap - The map that connects jump flags to number of jumps to get to them.
     */
    public Jumper(Map<String, Integer> jumpMap, Map<String, Integer> funcMap) {
        this.jumpMap = jumpMap;
        this.jumpFlagsLinksStack = new Stack<JumpFlagLinker>();
        this.funcMap = funcMap;
        this.returnLinesStack = new Stack<>();
        this.reader = null;
    }

    /**
     * @param reader - The reader that needs to be jumped.
     */
    public void setActiveReader(RestartableReader reader) {
        this.reader = reader;
    }

    /**
     * Makes the active reader jump to the correct jump flag.
     * @param jumpFlag - The jump flag to jump to.
     * @throws NullPointerException when the given jumpFlag doesn't exist in the processed file.
     * @throws UncheckedIOException when an exception that cannot be traced happened (but probably with the reader).
     */
    public void activeReaderJumpTo(String jumpFlag) {
        if (!jumpMap.containsKey(jumpFlag))
            throw new NullPointerException("שגיאה: לא נמצאה נקודת קפיצה בשם '" + jumpFlag + "'.");

        try {
            this.reader.restart();
            int targetLineNumber = this.jumpMap.get(jumpFlag);
            for (int lineCounter = 0; lineCounter < targetLineNumber; lineCounter++) {
                reader.readLine();
            }

        } catch (IOException exception) {
            throw new UncheckedIOException("שגיאה: הקפיצה נתקלה בשגיאה אל נקודת הקפיצה '" + jumpFlag + "'.", exception);
        }
    }

    public void activeReaderStartFunction(String function) {
        if (!funcMap.containsKey(function))
            throw new NullPointerException("שגיאה: לא נמצאה פונקציה בשם '" + function + "'.");

        try {
            this.returnLinesStack.push(this.reader.getCurrentLine());
            this.reader.restart();
            int targetLineNumber = this.funcMap.get(function);
            for (int lineCounter = 0; lineCounter < targetLineNumber; lineCounter++) {
                reader.readLine();
            }

        } catch (IOException exception) {
            throw new UncheckedIOException("שגיאה: הקפיצה נתקלה בשגיאה אל הפונקציה '" + function + "'.", exception);
        } //TODO: this looks almost the same as activeReaderJumpTo.... maybe fix it?
    }

    public void activeReaderReturnToCaller() {
        if (this.returnLinesStack.empty()) {
            throw new NullPointerException("אי אפשר לחזור מהסקופ הגלובילי.");
        }

        int returnLine = this.returnLinesStack.pop();
        try {
            this.reader.restart();
            for (int lineCounter = 1; lineCounter < returnLine; lineCounter++) {
                reader.readLine();
            }

        } catch (IOException exception) {
            throw new UncheckedIOException("שגיאה: חזרה מתוך פונקציה נכשלה.", exception);
        } //TODO: this looks almost the same as activeReaderJumpTo.... maybe fix it?
    }

    /**
     * Adds a new jump flag link.
     */
    public void addJumpFlagLink(String triggerFlag, String endFlag) {
        this.jumpFlagsLinksStack.add(new JumpFlagLinker(triggerFlag, endFlag));
    }

    /**
     * If the given string is the trigger jump flag of the nex jump flag link we are expecting, 
     * jumps the reader to the linked jump flag.
     * @param jumpFlag - The flag we read.
     */
    public void handleJumpFlagLinks(String jumpFlag) {
        if (this.jumpFlagsLinksStack.empty())
            return;

        if (jumpFlag.equals(this.jumpFlagsLinksStack.peek().triggerFlag)) {
            activeReaderJumpTo(this.jumpFlagsLinksStack.pop().endFlag);
        }
    }

    /**
     * An object that links a trigger jump flag to an end jump flag.
     */
    private class JumpFlagLinker {
        //The jump flag that starts the link.
        private String triggerFlag;
        //The jump flag to link to.
        private String endFlag;

        /**
         * Constructor.
         * @param triggerFlag - The jump flag that starts the link.
         * @param endFlag - The jump flag to link to.
         */
        private JumpFlagLinker(String triggerFlag, String endFlag) {
            this.triggerFlag = triggerFlag;
            this.endFlag = endFlag;
        }
    }
}
