package ru.giss.util.model.address;

/**
 * @author Ruslan Izmaylov
 */
public interface AddressFilter {

    boolean isSuitable(Address address);
}
