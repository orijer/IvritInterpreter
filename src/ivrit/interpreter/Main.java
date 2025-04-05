package ivrit.interpreter;

import java.util.Locale;

import ivrit.interpreter.UI.IvritInterpreterGUI;
import ivrit.interpreter.UI.UI;
import ivrit.interpreter.UserIO.ConsoleIO;
import ivrit.interpreter.UserIO.IvritIO;
import ivrit.interpreter.UserIO.UserInput;

/**
 * The main class the launches the interpreter.
 */
public class Main {
    public static void main(String[] args) {
        // Makes hebrew display properly in the GUI:
        System.setProperty("file.encoding", "UTF-8");
        Locale.setDefault(new Locale("he"));

        // Initiazlie what we need for the gui and the interpreter:
        UserInput userInput = new UserInput();
        IvritIO io = new ConsoleIO(userInput);
        UI ui = new IvritInterpreterGUI(io, userInput);
        SourceCodeLoader codeLoader = new FileSourceCodeLoader();
        FlowController controller = new FlowController(io, codeLoader);

        // Actually start running the interpreter:
        controller.startIvritInterpreter();

        // We finished everything.
        io.print("הפירוש הסתיים");
        io.print("ניתן לסגור את החלון בבטחה.");
    }
}
