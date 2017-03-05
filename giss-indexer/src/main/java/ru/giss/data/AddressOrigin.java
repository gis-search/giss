package ru.giss.data;

public interface AddressOrigin<T extends Address> extends AutoCloseable, Iterable<T> {
    int totalAddresses();
}
