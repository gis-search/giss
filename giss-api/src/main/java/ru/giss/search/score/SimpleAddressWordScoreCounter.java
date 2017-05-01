package ru.giss.search.score;

import ru.giss.search.SearchRequest;
import ru.giss.util.address.AddressWordInfo;

/**
 * @author Ruslan Izmaylov
 */
public class SimpleAddressWordScoreCounter implements ScoreCounter<AddressWordInfo> {

    public int count(SearchRequest req, int matchedGramCount, AddressWordInfo doc) {
        return matchedGramCount;
    }
}
