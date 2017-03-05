package ru.giss.indexer;

public interface AddressOrigin<T extends Address> extends AutoCloseable, Iterable<T> {
    int totalAddresses();
}
