package ru.giss.search;

/**
 * @author Ruslan Izmaylov
 */
public class Document<T> {

    private int id;
    private String term;
    private T item;

    public Document(int id, String term, T item) {
        this.id = id;
        this.term = term;
        this.item = item;
    }

    public int getId() {
        return id;
    }

    public String getTerm() {
        return term;
    }

    public T getItem() {
        return item;
    }
}
