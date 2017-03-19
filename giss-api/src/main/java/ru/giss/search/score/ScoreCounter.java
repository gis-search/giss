package ru.giss.search.score;

import ru.giss.model.Address;
import ru.giss.search.SearchRequest;

/**
 * Used to count scores dynamically while searching.
 *
 * @author Ruslan Izmaylov
 */
public interface ScoreCounter {

    int count(SearchRequest req, int matchedGramCount, Address doc);
}
