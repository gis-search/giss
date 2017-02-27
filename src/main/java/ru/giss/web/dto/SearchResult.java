package ru.giss.web.dto;

public class SearchResult {
    private final Long id;
    private final String address;

    public SearchResult(Long id, String address) {
        this.id = id;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }
}
