package ivrit.interpreter.IvritStreams;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashSet;

import javax.swing.JTextArea;

import java.awt.EventQueue;

/**
 * A stream that outputs to the TextArea.
 */
public class TextAreaOutputStream extends OutputStream {
    //The default amount of lines the console saves at the same time:
    public static final int defaultMaxConcurrentLines = 1000;

    //The object that control the text in the TextArea:
    private Appender appender;

    /**
     * Constructor.
     * @param textArea - the textArea object we want to write on.
     */
    public TextAreaOutputStream(JTextArea textArea) {
        this(textArea, defaultMaxConcurrentLines);
    }

    /**
     * Constructor.
     * @param textArea - the textArea object we want to write on.
     * @param maxConcurrentLines - the maximum amount of lines the console saves at the same time.
     */
    public TextAreaOutputStream(JTextArea textArea, int maxConcurrentLines) {
        if (maxConcurrentLines < 1) {
            throw new IllegalArgumentException(
                    "מספר השורות של הקונסול חייב להיות חיובי, אך התקבל " + maxConcurrentLines);
        }

        this.appender = new Appender(textArea, maxConcurrentLines);
    }

    /** 
     * Clear the current console text area.
     */
    public synchronized void clear() {
        if (this.appender != null) {
            this.appender.clear();
        }
    }

    /**
     * Closes this stream.
     */
    public synchronized void close() {
        this.appender = null;
    }

    /**
     * Writes to the TextArea when the data is represented as an integer.
     */
    public synchronized void write(int val) {
        byte[] byteArr = { (byte) val };
        write(byteArr, 0, 1);
    }

    /**
     * Writes to the TextArea when the data is represented as a byte array.
     */
    public synchronized void write(byte[] byteArr) {
        write(byteArr, 0, byteArr.length);
    }

    /**
     * Writes to the TextArea when the data is represented as a byte array, 
     * starting from a given offset for a given length.
     */
    public synchronized void write(byte[] byteArr, int offset, int len) {
        if (this.appender != null) {
            try { //We force the console to be slower so that it doesnt hurt the user's eyes (it was really annoying before...)
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //Do nothing!
            }

            //Decodes the byte array to a string and adds it at the end of the console:
            this.appender.append(bytesToString(byteArr, offset, len));            
        }
    }

    /**
     * Decodes a byte array to a string (in UTF-8).
     * @param byteArr - the byte array which contains the data.
     * @param offset - the index of the first byte to decode.
     * @param len - the number of bytes to decode from the array
     * @return the string represented by the given parameters.
     */
    private static String bytesToString(byte[] byteArr, int offset, int len) {
        return new String(byteArr, offset, len, StandardCharsets.UTF_8);
    }

    //Inner class:
    /**
     * The thread that is responsible for the behaviour of the console (like adding and clearing it).
     */
    private class Appender implements Runnable {
        //A set that contains all the end of line strings we use:
        private static final Set<String> endOfLineSet = new HashSet<String>(
                Arrays.asList("\n", System.getProperty("line.separator", "\n")));

        //The TextArea we want to write on:
        private final JTextArea textArea;
        //The maximum amount of lines the console will remember at a time:
        private final int maxLines;
        //A linked list that keeps the length of each line of the text area:
        private final Queue<Integer> lineLengthsQueue;
        //A list that contains the next strings waiting to be processed:
        private final List<String> unprocessedList;
        //The length of the current line:
        private int curLineLength;
        //true IFF the console is empty = has no text in it:
        private boolean isClear;
        //true IFF this is waiting for CPU time.
        private boolean isInQueue;

        /**
         * Construtor.
         * @param textArea - the TextArea we want to write on.
         * @param maxLines - the maximum amount of lines the console will remember at a time.
         */
        private Appender(JTextArea textArea, int maxLines) {
            this.textArea = textArea;
            this.maxLines = maxLines;
            this.lineLengthsQueue = new LinkedList<Integer>();
            this.unprocessedList = new LinkedList<String>();
            this.curLineLength = 0;
            this.isClear = false;
            this.isInQueue = false;
        }

        /**
         * Saves the given string to be processed later, and tries to queue this thread for running.
         * @param str - the string to be processed.
         */
        synchronized void append(String str) {
            this.unprocessedList.add(str);

            if (!this.isInQueue) {
                this.isInQueue = true;
                EventQueue.invokeLater(this);
            }
        }

        /**
         * Clears the console, and tries to queue this thread for running.
         */
        synchronized void clear() {
            this.isClear = true;
            this.curLineLength = 0;
            this.lineLengthsQueue.clear();
            this.unprocessedList.clear();

            if (!this.isInQueue) {
                this.isInQueue = true;
                EventQueue.invokeLater(this);
            }
        }

        /**
         * Updates the console with the next values needed.
         */
        public synchronized void run() {
            if (this.isClear) {
                this.textArea.setText("");
            }

            for (String str : this.unprocessedList) {
                this.curLineLength += str.length();
                if (hasReachedEndOfLine(str)) {
                    handlePassingMaxLines();

                    //Add the new line:
                    this.lineLengthsQueue.add(this.curLineLength);
                    this.curLineLength = 0;
                }
                this.textArea.append(str);

            }

            this.unprocessedList.clear();
            this.isClear = false;
            this.isInQueue = false;
        }

        /**
         * @param str - the string we want to check.
         * @return true IFF the given string ends with one of our EOL strings = the line ends with it.
         */
        private boolean hasReachedEndOfLine(String str) {
            for (String EOL : endOfLineSet) {
                if (str.endsWith(EOL)) {
                    return true;
                }
            }

            return false;
        }

        /**
         * The logic executed when we pass the maximum amount of lines the console holds at the same time.
         */
        private void handlePassingMaxLines() {
            if (this.lineLengthsQueue.size() >= this.maxLines) {
                int firstLineLength = this.lineLengthsQueue.remove();

                //Deletes the first line (since the given string is the empty string):
                this.textArea.replaceRange("", 0, firstLineLength);
            }
        }
    }
}
