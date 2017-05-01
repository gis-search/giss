package ru.giss.search;

/**
 * @author Ruslan Izmaylov
 */
public class Match<T> {

    private T doc;
    private int score;

    public Match(T doc, int score) {
        this.doc = doc;
        this.score = score;
    }

    public T getDoc() {
        return doc;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Match{" +
                "doc=" + doc +
                ", score=" + score +
                '}';
    }
}
