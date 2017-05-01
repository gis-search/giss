package ru.giss.util;

/**
 * @author Ruslan Izmaylov
 */
public class StringUtil {

    public static String normalize(String s, boolean preserveLength) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toLowerCase().toCharArray()) {
            if (c >= 'a' && c <= 'я' || Character.isDigit(c)) {
                sb.append(c);
            } else if (c == 'ё') {
                sb.append('е');
            } else if (preserveLength || Character.isWhitespace(c)) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    public static String[] nGrams(int n, String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n - 1; i++) {
            sb.append('|');
        }
        String newS = sb.append(s).toString();
        int size = newS.length() - n + 1;
        String[] res = new String[size];
        for (int i = 0; i < size; i++) {
            res[i] = newS.substring(i, i + n);
        }
        return res;
    }
}
