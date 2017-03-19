package ru.giss.search;

import static ru.giss.util.StringUtil.normalize;

/**
 * @author Ruslan Izmaylov
 */
public class SearchRequest {

    private String text;

    public SearchRequest(String rawText) {
        this.text = normalize(rawText);
    }

    public String getText() {
        return text;
    }
}
