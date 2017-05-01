package ru.giss.util.address;

import ru.giss.AddressModel;
import ru.giss.util.Searchable;

/**
 * @author Ruslan Izmaylov
 */
public class AddressWordInfo implements Searchable {

    private AddressModel.AddressWord word;
    private String name;
    private String[] synonyms;
    private AddressModel.AddressType addressType;

    public AddressWordInfo(AddressModel.AddressWord word, String name, String[] synonyms, AddressModel.AddressType addressType) {
        this.word = word;
        this.name = name;
        this.synonyms = synonyms;
        this.addressType = addressType;
    }

    public int getId() {
        return word.getNumber();
    }

    public AddressModel.AddressWord getWord() {
        return word;
    }

    public String getName() {
        return name;
    }

    public String[] getSynonyms() {
        return synonyms;
    }

    public AddressModel.AddressType getAddressType() {
        return addressType;
    }
}
