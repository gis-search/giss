package ru.giss.search.score;

import ru.giss.model.Address;
import ru.giss.search.SearchRequest;

/**
 * @author Ruslan Izmaylov
 */
public class SimpleAddressScoreCounter implements ScoreCounter<Address> {

    @Override
    public int count(SearchRequest req, int matchedGramCount, Address doc) {
        return matchedGramCount * 10000000 + doc.getScore();
    }
}
