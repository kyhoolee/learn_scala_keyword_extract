package id.co.babe.analysis.nlp;

import edu.stanford.nlp.util.Characters;

/**
 * Created by mainspring on 07/06/17.
 */
public class FilterUtils {

    public static String setenceFilter(String text) {
        String result = text.replace("/", " , ").replace(",", " , ")
                .replace("\"", "  ").replace("(", " , ").replace(")", " , ")
                .replace("\"", " , ").replace("“", " , ").replace("”", " , ")
                .replace("”", " , ").replace("‘", " , ").replace("’", " , ").replace("' ", " , ").replace(" '", " , ");

        result = removeLastPunctuation(result);

        return result;
    }

    public static String removeLastPunctuation(String sent) {
        String result = sent;

        if (result.length() > 0) {
            Character c = result.charAt(result.length() - 1);
            if (Characters.isPunctuation(c)) {
                result = result.substring(0, result.length() - 1);
            }
        }

        return result;
    }
}
