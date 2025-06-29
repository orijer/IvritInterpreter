package ivrit.interpreter.UI;

import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ivrit.interpreter.IvritStreams.TextAreaOutputStream;
import ivrit.interpreter.UserIO.IvritIO;
import ivrit.interpreter.UserIO.UserInput;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

/**
 * The GUI of the console that the Interpreter uses.
 * We allow printing messages from the program, and receiving input from the user when we need it.
 */
public class IvritInterpreterGUI extends AbstractUI {
    //The fonts used for the GUI:
    public static final Font TITLE_FONT = new Font("Ariel", Font.PLAIN, 40);
    public static final Font TEXT_FONT = new Font("Ariel", Font.PLAIN, 20);

    UserInput userInputObj;

    /**
     * Constructor.
     */
    public IvritInterpreterGUI(IvritIO io, UserInput userInputObj) {
        super(io);
        this.userInputObj = userInputObj;
        initializeFrame();
    }

    /**
     * @return the console frame after creating and adding to it all the elements needed.
     */
    private void initializeFrame() {
        JFrame consoleFrame = new JFrame();
        ImageIcon icon = new ImageIcon(getClass().getResource("icon.png"));
        consoleFrame.setIconImage(icon.getImage());
        consoleFrame.setSize(400, 300);
        consoleFrame.setVisible(true);
        consoleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        consoleFrame.add(initializeNorthBorder(), BorderLayout.NORTH);
        consoleFrame.add(initializeCenterBorder(), BorderLayout.CENTER);
        consoleFrame.add(initializeSouthBorder(), BorderLayout.SOUTH);
        consoleFrame.pack();

        consoleFrame.setVisible(true);
        Dimension max = Toolkit.getDefaultToolkit().getScreenSize();
        consoleFrame.setMaximumSize(max);
        consoleFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Initializes and returns the north part of the console frame.
     * @return - the north border of the console frame.
     */
    private Component initializeNorthBorder() {
        JLabel outputLabel = new JLabel("פלט התכנית:", SwingConstants.RIGHT);
        outputLabel.setFont(TITLE_FONT);

        return outputLabel;
    }

    /**
     * Initializes and returns the center part of the console frame.
     * @return - the center border of the console frame.
     */
    private Component initializeCenterBorder() {
        JTextArea consoleOutputText = new JTextArea("התחלה\n");
        consoleOutputText.setFont(TEXT_FONT);
        consoleOutputText.setEditable(false);
        consoleOutputText.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        TextAreaOutputStream consoleOutputStream = new TextAreaOutputStream(consoleOutputText);
        PrintStream consolePrintStream = new PrintStream(consoleOutputStream);
        System.setOut(consolePrintStream);
        System.setErr(consolePrintStream);

        return new JScrollPane(consoleOutputText);
    }

    /**
     * Initializes and returns the south part of the console frame.
     * @return - the south border of the console frame.
     */
    private Component initializeSouthBorder() {
        JLabel inputLabel = new JLabel("קלט מהמשתמש:", SwingConstants.RIGHT);
        inputLabel.setFont(TITLE_FONT);

        JTextField userInputField = new JTextField();
        userInputField.setFont(TEXT_FONT);
        userInputField.setHorizontalAlignment(JTextField.RIGHT);

        //Handles sending the text in the input field to the console itself
        userInputField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userInputObj.getIsUserInputAllowed()) {
                    userInputObj.newInputReceived(userInputField.getText());

                    //What the GUI does after an input:
                    io.print(userInputObj.getLastUserInput());
                    userInputField.setText("");
                }

            }
        });

        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.add(inputLabel);
        southPanel.add(userInputField);

        return southPanel;
    }
}
