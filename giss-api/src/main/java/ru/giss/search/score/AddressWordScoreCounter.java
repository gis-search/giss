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
        if (Math.abs(req.getText().length() - doc.getTerm().length()) > 1) {
            return -1;
        }
        double similarity = StringUtil.similarity(req.getText(), doc.getTerm());
        if (similarity < 0.85) {
            return -1;
        }
        return (int) (similarity * 1000);
    }
}
