package org.shizhz.util;

/**
 * This class <code>Joiner</code> is an utility class, used to join a collection
 * of elements to a String with specified delimiter
 * 
 * @author shizhz
 * 
 */
public final class Joiner {

    private String delimiter;

    private Joiner(String delimiter) {
        this.delimiter = delimiter;
    }

    public static Joiner on(String delimiter) {
        return new Joiner(delimiter);
    }

    /**
     * Join an Array of Strings to a single String with specified delimiter
     * 
     * @param stringArray
     * @return
     */
    public String join(String[] stringArray) {
        if (stringArray == null || stringArray.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (String element : stringArray) {
            result.append(element);
            result.append(delimiter);
        }

        return result.deleteCharAt(result.length() - 1).toString();
    }
}
