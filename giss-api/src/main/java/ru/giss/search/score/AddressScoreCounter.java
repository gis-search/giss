package ru.giss.search.score;

import ru.giss.AddressModel.AddressWordWithPosition;
import ru.giss.search.Document;
import ru.giss.search.request.AddressSearchRequest;
import ru.giss.util.StringUtil;
import ru.giss.util.model.address.Address;

import static java.lang.Character.isDigit;

/**
 * @author Ruslan Izmaylov
 */
public class AddressScoreCounter implements ScoreCounter<Document<Address>, AddressSearchRequest> {

    @Override
    public long count(AddressSearchRequest req, Document<Address> doc) {
        if (Math.abs(req.getText().length() - doc.getTerm().length()) > 1) {
            return -1;
        }
        if (isDigit(req.getText().charAt(req.getText().length() - 1)) && !isDigit(doc.getTerm().charAt(doc.getTerm().length() - 1))) {
            return -1;
        }

        boolean parentMatched = req.getOptParent().map(parent -> {
            Address cur = doc.getItem();
            while (cur != null) {
                if (cur.getId() == parent.getId()) {
                    return true;
                }
                cur = cur.getParent();
            }
            return false;
        }).orElse(true);
        if (!parentMatched) {
            return -1;
        }

        double similarity = StringUtil.similarity(req.getText(), doc.getTerm());
        if (similarity < 0.85) {
            return -1;
        }

        int addressWordScore = 0;
        for (AddressWordWithPosition item : doc.getItem().getAddressWordsWithPosition()) {
            if (req.getAddressWords().contains(item.getWord())) {
                addressWordScore += 1;
            }
        }

        return (int) (similarity * 1000000000L) +
                addressWordScore * 100000000L +
                doc.getItem().getScore();
    }
}
