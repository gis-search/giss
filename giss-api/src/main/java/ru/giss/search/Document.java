package ru.giss.search;

import java.util.Map;

import static ru.giss.util.StringUtil.nGramMap;
import static ru.giss.util.StringUtil.normalize;

/**
 * @author Ruslan Izmaylov
 */
public class Document<T> {

    private int id;
    private String term;
    private T item;
    private Map<String, Integer> grams;


    public Document(int gramLength, int id, String term, T item) {
        this.id = id;
        this.term = term;
        this.item = item;
        this.grams = nGramMap(gramLength, normalize(term, true));
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

    public Map<String, Integer> getGrams() {
        return grams;
    }
}
