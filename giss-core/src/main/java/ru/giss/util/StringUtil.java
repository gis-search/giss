package ru.giss.util;

/**
 * @author Ruslan Izmaylov
 */
public class StringUtil {

    public static String normalize(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toLowerCase().toCharArray()) {
            if (c >= 'a' && c <= 'я' || Character.isDigit(c) || c == ' ') {
                sb.append(c);
            } else if (c == 'ё') {
                sb.append('е');
            }
        }
        return sb.toString();
    }

    public static String[] nGrams(int n, String s) {
        if (n > s.length()) return new String[] {};
        int size = s.length() - n + 1;
        String[] res = new String[size];
        for (int i = 0; i < size; i++) {
            res[i] = s.substring(i, i + n);
        }
        return res;
    }
}
