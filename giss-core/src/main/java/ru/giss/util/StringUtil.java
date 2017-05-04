package ru.giss.util;

import org.pcollections.PVector;
import org.pcollections.TreePVector;
import ru.giss.util.model.token.Token;
import ru.giss.util.model.token.UndefinedToken;

import java.util.*;

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

    public static Map<String, Integer> nGramMap(int n, String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n - 1; i++) {
            sb.append('|');
        }
        String newS = sb.append(s).toString();
        int size = newS.length() - n + 1;
        Map<String, Integer> res = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            String gram = newS.substring(i, i + n);
            int count = res.getOrDefault(gram, 0);
            res.put(gram, count + 1);
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

    /**
     * Proposed by Esko Ukkonen
     */
    public static int gramDistance(Map<String, Integer> g1, Map<String, Integer> g2) {
        Set<String> gs = new HashSet<>(g1.keySet());
        gs.addAll(g2.keySet());
        int res = 0;
        for (String g : gs) {
            res += Math.abs(g1.getOrDefault(g, 0) - g2.getOrDefault(g, 0));
        }
        return res;
    }

    /**
     * Gram distance from 0.0 to 1.0.
     * 0.0 means string are identical.
     */
    public static double normGramDistance(Map<String, Integer> g1, Map<String, Integer> g2) {
        int gd = gramDistance(g1, g2);
        int maxGd = 0;
        for (Integer count : g1.values()) {
            maxGd += count;
        }
        for (Integer count : g2.values()) {
            maxGd += count;
        }
        return maxGd == 0 ? 0 : (double) gd / maxGd;
    }
}
