package ru.giss.search;

import ru.giss.model.Address;

/**
 * @author Ruslan Izmaylov
 */
public class Match {

    private Address doc;
    private int score;

    public Match(Address doc, int score) {
        this.doc = doc;
        this.score = score;
    }

    public Address getDoc() {
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
