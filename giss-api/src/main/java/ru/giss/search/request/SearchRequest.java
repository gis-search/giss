package ru.giss.search.request;

import ru.giss.util.StringUtil;

import java.util.Map;

import static ru.giss.util.StringUtil.normalize;

/**
 * @author Ruslan Izmaylov
 */
public class SearchRequest {

    protected String text;
    protected int gramLength;
    protected Map<String, Integer> grams;

    public SearchRequest(String rawText, int gramLength) {
        this.text = normalize(rawText, false);;
        this.gramLength = gramLength;
        this.grams = StringUtil.nGramMap(gramLength, text);
    }

    public String getText() {
        return text;
    }

    public Map<String, Integer> getGrams() {
        return grams;
    }
}
