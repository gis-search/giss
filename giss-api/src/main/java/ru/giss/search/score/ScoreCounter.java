package ru.giss.search.score;

import ru.giss.search.SearchRequest;

/**
 * Used to count scores dynamically while searching.
 *
 * @author Ruslan Izmaylov
 */
public interface ScoreCounter<T> {

    int count(SearchRequest req, int matchedGramCount, T doc);
}
