package Evaluation;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles dereferencing values from list variables (changes "[1, 3, 9] at 2" to 3).
 */
public class ListDereferencer {
    public static String dereferenceStringsList(String list, int index) {
        list = list.substring(1, list.length()-1);
        List<String> splitList = new ArrayList<String>();

        int start = 0;
        int i = 1;
        while (i < list.length()) {
            if (list.charAt(i) == '\"') {
                splitList.add(list.substring(start, i + 1));

                i++;
                while (i < list.length() && (list.charAt(i) == ' ' || list.charAt(i) == ','))
                    i++;

                start = i;
                i++;
            } else {
                i++;
            }
        }

        return splitList.get(index-1);
    }

    public static String dereferenceNonStringsList(String list, int index) {
        list = list.substring(1, list.length()-1);
        String[] values = list.split(",");
        return values[index-1].trim();
    }
}
