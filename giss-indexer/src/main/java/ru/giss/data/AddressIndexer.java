package ru.giss.data;

public interface AddressIndexer<T extends Address> extends AutoCloseable {
    void index(T address);
}
