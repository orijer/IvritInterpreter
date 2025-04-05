package ivrit.interpreter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * A SourceFileLoader that transforms Ivrit code written in a file into a SourceFile object.
 */
public class FileSourceCodeLoader implements SourceCodeLoader {

    /**
     * Constructor.
     * @param io - The io object responsible for getting input&output to the user.
     */
    public FileSourceCodeLoader() {
        // currently empty.
    }

    @Override
    public SourceFile load(String str) throws IllegalArgumentException {
        str = handleCopyingAddsQuotationMark(str);
        Path path = Paths.get(str);
        if (!Files.exists(path))
            throw new IllegalArgumentException("הקובץ לא נמצא!");

        // Add all code lines (no empty lines or comments) of the file to a list:
        List<String> lines = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.charAt(0) == SourceCodeLoader.COMMENT_SYMBOL)
                    continue;

                lines.add(line);
            }

            return new SourceFile(lines);
        } catch (IOException exception) {
            throw new UncheckedIOException("שגיאה: נמצאה שגיאה בזמן קריאת הקלט. וודאו שהוא אינו פתוח בתהליך אחר.", exception);
        }
    }
    
    /**
     * When you ask windows to copy the path of the file it adds quotation marks around the real path.
     * This method detects if a string is in quotation marks, and if so it returns the substring without them.
     * Else (= the string wasn't in quotation), we return it unchanged.
     * @param original - The original string.
     * @return - The formatted paths.
     */
    private String handleCopyingAddsQuotationMark(String original) {
        if (original.charAt(0) == '"' && original.charAt(original.length() - 1) == '"') {
            return original.substring(1, original.length() - 1);
        }

        return original;
    }
}
