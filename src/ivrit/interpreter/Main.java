package ivrit.interpreter;

import java.util.Locale;

/**
 * The main class the launches the interpreter.
 */
public class Main {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        Locale.setDefault(new Locale("he"));

        FlowController controller = new FlowController();
        controller.startIvritInterpreter(true);

        System.out.println("הפירוש הסתיים");
        System.out.println("ניתן לסגור את החלון בבטחה.");
    }
} // Fix a bug where the code משתנה מ = 5 doesn't print the error correctly...
