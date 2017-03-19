package ru.giss.app;

import ru.giss.search.Searcher;

/**
 * @author Ruslan Izmaylov
 */
public class Backend {

    private final Searcher citySearcher;

    Backend(Searcher citySearcher) {
        this.citySearcher = citySearcher;
    }

    public Searcher getCitySearcher() {
        return citySearcher;
    }
}
