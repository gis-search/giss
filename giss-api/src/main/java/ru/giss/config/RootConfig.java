package ru.giss.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import ru.giss.AddressModel;
import ru.giss.search.Document;
import ru.giss.search.Searcher;
import ru.giss.search.parsing.Parser;
import ru.giss.search.request.SearchRequest;
import ru.giss.search.score.AddressScoreCounter;
import ru.giss.search.score.AddressWordScoreCounter;
import ru.giss.util.model.address.Address;
import ru.giss.util.model.address.AddressWordInfo;
import ru.giss.util.model.address.HouseInfo;
import ru.giss.util.model.address.IndexedAddressedWords;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import static ru.giss.AddressModel.AddressType.AT_HOUSE;
import static ru.giss.util.StringUtil.nGramSet;
import static ru.giss.util.StringUtil.normalize;

@Configuration
@ComponentScan(basePackages = "ru.giss")
@PropertySource("classpath:core.properties")
public class RootConfig {
    private static final Logger LOG = LoggerFactory.getLogger(RootConfig.class);

    @Value("${giss.addresses.proto}")
    private String gissFile;

    @Value("${giss.gram-length:2}")
    private int gramLength;

    public final static String NUMBER_REGEX_GROUP = "number";
    public final static String BUILDING_REGEX_GROUP = "building";
    public final static Pattern HOUSE_REGEX =
            Pattern.compile("(?<" + NUMBER_REGEX_GROUP + ">\\d{1,4}[а-я&&[^кс]]?) ?(?:[кс\\-](?<" + BUILDING_REGEX_GROUP + ">\\d{1,2}))?");

    @Bean
    public Parser getParser() throws IOException {
        LOG.info("Starting building indices...");

        try(InputStream is = new BufferedInputStream(new GZIPInputStream(new FileInputStream(gissFile)))) {
            Map<String, ArrayList<Document<Address>>> regionIndex = new HashMap<>();
            Map<String, ArrayList<Document<Address>>> cityIndex = new HashMap<>();
            Map<String, ArrayList<Document<Address>>> villageIndex = new HashMap<>();
            Map<String, ArrayList<Document<Address>>> streetIndex = new HashMap<>();
            Map<Address, HouseInfo> streetToHouseInfo = new HashMap<>();
            AddressModel.AddressMsg msg;
            ArrayList<Address> nodes = new ArrayList<>(6700000);
            ArrayList<Document<Address>> docs = new ArrayList<>(500000);

            nodes.add(null);
            int curDocId = 0;
            while ((msg = AddressModel.AddressMsg.parseDelimitedFrom(is)) != null) {
                if (msg.getId() % 100000 == 0) System.out.println("Processing id " + msg.getId());
                Address parent = nodes.get(msg.getParentId());
                Address node = new Address(
                        msg.getId(),
                        parent,
                        msg.getName(),
                        msg.getAddressWordWithPositionList(),
                        msg.getType(),
                        msg.getLatitude(),
                        msg.getLongitude(),
                        msg.getChildCount(),
                        msg.getPopulation());
                nodes.add(node);
                if (msg.getType() != AT_HOUSE) {
                    Map<String, ArrayList<Document<Address>>> index;
                    switch (msg.getType()) {
                        case AT_REGION: index = regionIndex; break;
                        case AT_CITY: index = cityIndex; break;
                        case AT_VILLAGE: index = villageIndex; break;
                        case AT_STREET: index = streetIndex; break;
                        default: continue;
                    }
                    String normName = normalize(node.getName(), true);
                    Document<Address> doc = new Document<>(curDocId++, normName, node);
                    docs.add(doc);
                    Set<String> nGrams = nGramSet(gramLength, normName);
                    for (String gram : nGrams) {
                        index.computeIfAbsent(gram, k -> new ArrayList<>()).add(doc);
                    }
                } else {
                    Matcher matcher = HOUSE_REGEX.matcher(msg.getName().toLowerCase());
                    HouseInfo houseInfo = streetToHouseInfo.computeIfAbsent(parent, k -> new HouseInfo(new HashMap<>()));
                    if (matcher.find()) {
                        String number = matcher.group(NUMBER_REGEX_GROUP);
                        String building = matcher.group(BUILDING_REGEX_GROUP);
                        Map<String, Address> buildingMap = houseInfo.getNumberToBuildings().computeIfAbsent(number, k -> new HashMap<>());
                        buildingMap.put(building, node); // building may be null
                    } else {
                        houseInfo.getNumberToBuildings().putIfAbsent(msg.getName(), Collections.singletonMap(null, node));
                    }
                }
            }
            Searcher<AddressWordInfo, SearchRequest> addrWordSearcher = indexAddrWords();
            LOG.info("Finished building indices");

            Backend backend =  new Backend(
                    gramLength,
                    addrWordSearcher,
                    new Searcher<>(regionIndex, docs, new AddressScoreCounter()),
                    new Searcher<>(cityIndex, docs, new AddressScoreCounter()),
                    new Searcher<>(villageIndex, docs, new AddressScoreCounter()),
                    new Searcher<>(streetIndex, docs, new AddressScoreCounter()),
                    streetToHouseInfo);
            return new Parser(backend);
        }
    }

    private Searcher<AddressWordInfo, SearchRequest> indexAddrWords() {
        Map<String, ArrayList<Document<AddressWordInfo>>> index = new HashMap<>();
        Set<AddressWordInfo> addrWords = new TreeSet<>(Comparator.comparingInt(AddressWordInfo::getId));
        addrWords.addAll(IndexedAddressedWords.getAddressWordToInfo().values());
        ArrayList<Document<AddressWordInfo>> docs = new ArrayList<>();
        int curDocId = 0;
        for (AddressWordInfo info : addrWords) {
            List<String> terms = new ArrayList<>(info.getSynonyms().length + 1);
            terms.addAll(Arrays.asList(info.getSynonyms()));
            terms.add(info.getName());
            for (String term : terms) {
                term = normalize(term, true);
                Document<AddressWordInfo> doc = new Document<>(curDocId++, term, info);
                docs.add(doc);
                Set<String> nGrams = nGramSet(gramLength, term);
                for (String gram : nGrams) {
                    index.computeIfAbsent(gram, k -> new ArrayList<>()).add(doc);
                }
            }
        }
        return new Searcher<>(index, docs, new AddressWordScoreCounter());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
