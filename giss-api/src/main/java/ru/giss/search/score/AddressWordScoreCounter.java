package ru.giss.search.score;

import ru.giss.search.Document;
import ru.giss.search.request.SearchRequest;
import ru.giss.util.StringUtil;
import ru.giss.util.model.address.AddressWordInfo;

/**
 * @author Ruslan Izmaylov
 */
public class AddressWordScoreCounter implements ScoreCounter<Document<AddressWordInfo>, SearchRequest> {

    public long count(SearchRequest req, Document<AddressWordInfo> doc) {
        if (Math.abs(req.getText().length() - doc.getTerm().length()) > 2) {
            return -1;
        }
        double gramDist = StringUtil.normGramDistance(req.getGrams(), doc.getGrams());
        if (gramDist > 0.2) {
            return -1;
        }
        return (int) ((1 - gramDist) * 1000);
    }
}
