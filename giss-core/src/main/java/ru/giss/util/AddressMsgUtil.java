package ru.giss.util;

import ru.giss.AddressModel;

/**
 * @author Ruslan Izmaylov
 */
public class AddressMsgUtil {

    public static String getName(AddressModel.AddressMsg item) {
        switch (item.getType()) {
            case HOUSE: return item.getHouseNumber();
            case STREET: return item.getStreet();
            case DISTRICT: return item.getDistrict();
            case VILLAGE: return item.getVillage();
            case CITY: return item.getCity();
            case LOCALITY: return item.getLocality();
            case AREA: return item.getArea();
            case REGION: return item.getRegion();
            case COUNTRY: return item.getCountry();
            default: throw new IllegalArgumentException("Type " + item.getType() + " is unsupported");
        }
    }
}
