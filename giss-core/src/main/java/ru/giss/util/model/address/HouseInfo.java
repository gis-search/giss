package ru.giss.util.model.address;

import java.util.Map;

/**
 * Information about all houses on a street
 *
 * @author Ruslan Izmaylov
 */
public class HouseInfo {

    /**
     * A mapping from house number to a mapping from building name to corresponding address.
     * If a house is a single building, the inner map contains a single key 'null'.
     */
    private Map<String, Map<String, Address>> numberToBuildings;

    public HouseInfo(Map<String, Map<String, Address>> numberToBuildings) {
        this.numberToBuildings = numberToBuildings;
    }

    public Map<String, Map<String, Address>> getNumberToBuildings() {
        return numberToBuildings;
    }
}
