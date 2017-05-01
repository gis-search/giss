package ru.giss.model;

import ru.giss.AddressModel.AddressType;
import ru.giss.AddressModel.AddressWordWithPosition;
import ru.giss.util.Searchable;
import ru.giss.util.address.IndexedAddressedWords;

import java.util.List;

/**
 * @author Ruslan Izmaylov
 */
public class Address implements Searchable {

    private final int id;
    private final Address parent;
    private final String name;
    private final List<AddressWordWithPosition> addressWordsWithPosition;
    private final AddressType type;
    private final float lat;
    private final float lon;

    private int score;

    public Address(int id,
                   Address parent,
                   String name,
                   List<AddressWordWithPosition> addressWordsWithPosition,
                   AddressType type,
                   float lat,
                   float lon,
                   int childCount,
                   int population) {
        this.id = id;
        this.parent = parent;
        this.name = name;
        this.addressWordsWithPosition = addressWordsWithPosition;
        this.type = type;
        this.lat = lat;
        this.lon = lon;

        // TODO better scoring
        if (type == AddressType.AT_CITY) {
            score = population + parent.score;
        } else if (type == AddressType.AT_COUNTRY) {
            score = 0;
        } else {
            score = childCount;
        }
    }

    public int getId() {
        return id;
    }

    public Address getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public List<AddressWordWithPosition> getAddressWordsWithPosition() {
        return addressWordsWithPosition;
    }

    public String fullName() {
        String parentName = parent == null ? "" : (parent.fullName() + ", ");
        String prefix = "";
        String suffix = "";
        for (AddressWordWithPosition wordWithPos : addressWordsWithPosition) {
            String word = IndexedAddressedWords.getAddressWordToInfo().get(wordWithPos.getWord()).getName();
            if (wordWithPos.getIsPrefix()) {
                prefix += word + " ";
            } else {
                suffix += " " + word;
            }
        }
        return parentName + prefix + name + suffix;
    }

    public AddressType getType() {
        return type;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
