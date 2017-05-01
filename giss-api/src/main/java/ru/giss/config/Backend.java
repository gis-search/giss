package ru.giss.config;

import ru.giss.model.Address;
import ru.giss.search.Searcher;
import ru.giss.util.address.AddressWordInfo;

/**
 * @author Ruslan Izmaylov
 */
public class Backend {

    private Searcher<AddressWordInfo> addrWordSearcher;
    private Searcher<Address> regionSearcher;
    private Searcher<Address> citySearcher;
    private Searcher<Address> villageSearcher;
    private Searcher<Address> streetSearcher;

    public Backend(Searcher<AddressWordInfo> addrWordSearcher, Searcher<Address> regionSearcher, Searcher<Address> citySearcher, Searcher<Address> villageSearcher, Searcher<Address> streetSearcher) {
        this.addrWordSearcher = addrWordSearcher;
        this.regionSearcher = regionSearcher;
        this.citySearcher = citySearcher;
        this.villageSearcher = villageSearcher;
        this.streetSearcher = streetSearcher;
    }

    public Searcher<AddressWordInfo> getAddrWordSearcher() {
        return addrWordSearcher;
    }

    public Searcher<Address> getRegionSearcher() {
        return regionSearcher;
    }

    public Searcher<Address> getCitySearcher() {
        return citySearcher;
    }

    public Searcher<Address> getVillageSearcher() {
        return villageSearcher;
    }

    public Searcher<Address> getStreetSearcher() {
        return streetSearcher;
    }
}
