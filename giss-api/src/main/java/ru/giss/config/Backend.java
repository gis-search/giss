package ru.giss.config;

import ru.giss.search.Searcher;
import ru.giss.search.request.AddressSearchRequest;
import ru.giss.search.request.SearchRequest;
import ru.giss.util.model.address.Address;
import ru.giss.util.model.address.AddressWordInfo;
import ru.giss.util.model.address.HouseInfo;

import java.util.Map;

/**
 * @author Ruslan Izmaylov
 */
public class Backend {

    private int gramLength;
    private Searcher<AddressWordInfo, SearchRequest> addrWordSearcher;
    private Searcher<Address, AddressSearchRequest> regionSearcher;
    private Searcher<Address, AddressSearchRequest> citySearcher;
    private Searcher<Address, AddressSearchRequest> villageSearcher;
    private Searcher<Address, AddressSearchRequest> streetSearcher;
    private Map<Address, HouseInfo> streetToHouseInfo;

    public Backend(int gramLength,
                   Searcher<AddressWordInfo, SearchRequest> addrWordSearcher,
                   Searcher<Address, AddressSearchRequest> regionSearcher,
                   Searcher<Address, AddressSearchRequest> citySearcher,
                   Searcher<Address, AddressSearchRequest> villageSearcher,
                   Searcher<Address, AddressSearchRequest> streetSearcher,
                   Map<Address, HouseInfo> streetToHouseInfo) {
        this.gramLength = gramLength;
        this.addrWordSearcher = addrWordSearcher;
        this.regionSearcher = regionSearcher;
        this.citySearcher = citySearcher;
        this.villageSearcher = villageSearcher;
        this.streetSearcher = streetSearcher;
        this.streetToHouseInfo = streetToHouseInfo;
    }

    public int getGramLength() {
        return gramLength;
    }

    public Searcher<AddressWordInfo, SearchRequest> getAddrWordSearcher() {
        return addrWordSearcher;
    }

    public Searcher<Address, AddressSearchRequest> getRegionSearcher() {
        return regionSearcher;
    }

    public Searcher<Address, AddressSearchRequest> getCitySearcher() {
        return citySearcher;
    }

    public Searcher<Address, AddressSearchRequest> getVillageSearcher() {
        return villageSearcher;
    }

    public Searcher<Address, AddressSearchRequest> getStreetSearcher() {
        return streetSearcher;
    }

    public Map<Address, HouseInfo> getStreetToHouseInfo() {
        return streetToHouseInfo;
    }
}
