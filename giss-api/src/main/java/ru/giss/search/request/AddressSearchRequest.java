package ru.giss.search.request;

import org.pcollections.HashTreePSet;
import ru.giss.AddressModel.AddressWord;
import ru.giss.util.model.address.Address;

import java.util.Optional;
import java.util.Set;

/**
 * @author Ruslan Izmaylov
 */
public class AddressSearchRequest extends SearchRequest {

    private Set<AddressWord> addressWords;
    private Optional<Address> optParent;

    public AddressSearchRequest(String rawText,
                                int gramLength,
                                Set<AddressWord> addressWords,
                                Optional<Address> optParent) {
        super(rawText, gramLength);
        this.addressWords = addressWords;
        this.optParent = optParent;
    }

    public AddressSearchRequest(String rawText, int gramLength) {
        this(rawText, gramLength, HashTreePSet.empty(), Optional.empty());
    }

    public Set<AddressWord> getAddressWords() {
        return addressWords;
    }

    public Optional<Address> getOptParent() {
        return optParent;
    }
}
