package ru.giss.search.request;

import ru.giss.util.StringUtil;

import java.util.Set;

import static ru.giss.util.StringUtil.normalize;

/**
 * @author Ruslan Izmaylov
 */
public class SearchRequest {

    protected String text;
    protected int gramLength;
    protected Set<String> grams;

    public SearchRequest(String rawText, int gramLength) {
        this.text = normalize(rawText, false);;
        this.gramLength = gramLength;
        this.grams = StringUtil.nGramSet(gramLength, text);
    }

    public String getText() {
        return text;
    }

    public Set<String> getGrams() {
        return grams;
    }
}
