package ru.giss.web.dto;

import ru.giss.util.model.address.Address;

public class SearchResult {
    private final String address;
    private final float lat;
    private final float lon;

    public SearchResult(Address address) {
        this(address.fullName(), address.getLat(), address.getLon());
    }

    public SearchResult(String address, float lat, float lon) {
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}
