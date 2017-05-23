package ru.giss.search;

/**
 * @author Ruslan Izmaylov
 */
public class Match<T> {

    private T doc;
    private long score;

    public Match(T doc, long score) {
        this.doc = doc;
        this.score = score;
    }

    public T getDoc() {
        return doc;
    }

    public long getScore() {
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
