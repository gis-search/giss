package ru.giss.model;

import ru.giss.AddressModel.AddressType;

/**
 * @author Ruslan Izmaylov
 */
public class Address {

    private final int id;
    private final Address parent;
    private final String name;
    private final AddressType type;
    private final float lat;
    private final float lon;

    private int score;

    public Address(int id,
                   Address parent,
                   String name,
                   AddressType type,
                   float lat,
                   float lon,
                   int childCount,
                   int population) {
        this.id = id;
        this.parent = parent;
        this.name = name;
        this.type = type;
        this.lat = lat;
        this.lon = lon;

        // TODO better scoring
        if (type == AddressType.CITY) {
            score = population + parent.score;
        } else if (type == AddressType.COUNTRY) {
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

    public String fullName() {
        String prefix = parent == null ? "" : (parent.fullName() + ", ");
        return prefix + name;
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
