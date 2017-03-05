package ru.giss.indexer;

public interface AddressIndexer<T extends Address> extends AutoCloseable {
    void index(T address);
}
