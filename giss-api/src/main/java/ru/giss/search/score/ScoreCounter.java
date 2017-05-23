package ru.giss.search.score;

import ru.giss.search.Document;
import ru.giss.search.request.SearchRequest;

/**
 * Used to count scores dynamically while searching.
 *
 * @author Ruslan Izmaylov
 */
public interface ScoreCounter<T extends Document, R extends SearchRequest> {

    /**
     * @return positive number that denotes document relevance
     *         or negative number if the document should be ignored
     */
    long count(R req, T doc);
}
