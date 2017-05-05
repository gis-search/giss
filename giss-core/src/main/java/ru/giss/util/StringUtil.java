package ru.giss.util;

import info.debatty.java.stringsimilarity.JaroWinkler;
import org.pcollections.PVector;
import org.pcollections.TreePVector;
import ru.giss.util.model.token.Token;
import ru.giss.util.model.token.UndefinedToken;

import java.util.*;

/**
 * @author Ruslan Izmaylov
 */
public class StringUtil {

    private final static JaroWinkler STRING_SIMILARITY = new JaroWinkler();

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

    public static Set<String> nGramSet(int n, String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n - 1; i++) {
            sb.append('|');
        }
        String newS = sb.append(s).toString();
        int size = newS.length() - n + 1;
        Set<String> res = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            res.add(newS.substring(i, i + n));
        }
        return res;
    }

    public static PVector<Token> tokenize(String s) {
        String[] split = s.split("[\\s-]+");
        List<Token> list = new ArrayList<>(split.length);
        for (int i = 0; i < split.length; i++) {
            list.add(new Token(i, split[i], UndefinedToken.INSTANCE));
        }
        return TreePVector.from(list);
    }

    public static String join(List<Token> tokens, int from, int to) {
        if (from < 0 || to < 0 || from >= to) throw new IllegalArgumentException("Bad arguments: from=" + from + ", to=" + to);
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < to; i++) {
            sb.append(tokens.get(i).getString()).append(' ');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static double similarity(String s1, String s2) {
        return STRING_SIMILARITY.similarity(s1, s2);
    }

// Gram distance is bad
//    /**
//     * Proposed by Esko Ukkonen
//     */
//    public static int gramDistance(Map<String, Integer> g1, Map<String, Integer> g2) {
//        Set<String> gs = new HashSet<>(g1.keySet());
//        gs.addAll(g2.keySet());
//        int res = 0;
//        for (String g : gs) {
//            res += Math.abs(g1.getOrDefault(g, 0) - g2.getOrDefault(g, 0));
//        }
//        return res;
//    }
//
//    /**
//     * Gram distance from 0.0 to 1.0.
//     * 0.0 means string are identical.
//     */
//    public static double normGramDistance(Map<String, Integer> g1, Map<String, Integer> g2) {
//        int gd = gramDistance(g1, g2);
//        int maxGd = 0;
//        for (Integer count : g1.values()) {
//            maxGd += count;
//        }
//        for (Integer count : g2.values()) {
//            maxGd += count;
//        }
//        return maxGd == 0 ? 0 : (double) gd / maxGd;
//    }
}
