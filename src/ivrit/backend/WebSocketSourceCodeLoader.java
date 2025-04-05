package ivrit.backend;

import java.util.LinkedList;
import java.util.List;

import ivrit.interpreter.SourceCodeLoader;
import ivrit.interpreter.SourceFile;

/**
 * A SourceFileLoader that transforms a string of code directly into a SourceFile object.
 */
public class WebSocketSourceCodeLoader implements SourceCodeLoader {

    @Override
    public SourceFile load(String str) throws IllegalArgumentException {
        String[] lines = str.split("\n");
        List<String> codeLines = new LinkedList<>();

        for (String line : lines) {
            if (line.isBlank() || line.charAt(0) == SourceCodeLoader.COMMENT_SYMBOL)
                continue;

            codeLines.add(line);
        }

        return new SourceFile(codeLines);
    }
    
}
