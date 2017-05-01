package ru.giss.config;

import ru.giss.model.Address;
import ru.giss.search.Searcher;
import ru.giss.util.address.AddressWordInfo;

/**
 * @author Ruslan Izmaylov
 */
public class Backend {

    private Searcher<AddressWordInfo> addrWordSearcher;
    private Searcher<Address> citySearcher;

    public Backend(Searcher<AddressWordInfo> addrWordSearcher, Searcher<Address> citySearcher) {
        this.addrWordSearcher = addrWordSearcher;
        this.citySearcher = citySearcher;
    }

    public Searcher<AddressWordInfo> getAddrWordSearcher() {
        return addrWordSearcher;
    }

    public Searcher<Address> getCitySearcher() {
        return citySearcher;
    }
}
