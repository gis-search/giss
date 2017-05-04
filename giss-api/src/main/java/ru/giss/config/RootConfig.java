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
import ru.giss.util.model.address.IndexedAddressedWords;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static ru.giss.AddressModel.AddressType.AT_HOUSE;

@Configuration
@ComponentScan(basePackages = "ru.giss")
@PropertySource("classpath:core.properties")
public class RootConfig {
    private static final Logger LOG = LoggerFactory.getLogger(RootConfig.class);

    @Value("${giss.addresses.proto}")
    private String gissFile;

    @Value("${giss.gram-length:2}")
    private int gramLength;

    @Bean
    public Parser getParser() throws IOException {
        LOG.info("Starting building indices...");

        try(InputStream is = new BufferedInputStream(new GZIPInputStream(new FileInputStream(gissFile)))) {
            Map<String, ArrayList<Document<Address>>> regionIndex = new HashMap<>();
            Map<String, ArrayList<Document<Address>>> cityIndex = new HashMap<>();
            Map<String, ArrayList<Document<Address>>> villageIndex = new HashMap<>();
            Map<String, ArrayList<Document<Address>>> streetIndex = new HashMap<>();
            AddressModel.AddressMsg msg;
            ArrayList<Address> nodes = new ArrayList<>(6700000);
            ArrayList<Document<Address>> docs = new ArrayList<>(500000);
            nodes.add(null);
            int curDocId = 0;
            while ((msg = AddressModel.AddressMsg.parseDelimitedFrom(is)) != null) {
                Address node = new Address(
                        msg.getId(),
                        nodes.get(msg.getParentId()),
                        msg.getName(),
                        msg.getAddressWordWithPositionList(),
                        msg.getType(),
                        msg.getLatitude(),
                        msg.getLongitude(),
                        msg.getChildCount(),
                        msg.getPopulation());
                nodes.add(node);
                Map<String, ArrayList<Document<Address>>> index;
                switch (msg.getType()) {
                    case AT_REGION: index = regionIndex; break;
                    case AT_CITY: index = cityIndex; break;
                    case AT_VILLAGE: index = villageIndex; break;
                    case AT_STREET: index = streetIndex; break;
                    default: continue;
                }
                if (msg.getType() != AT_HOUSE) {
                    Document<Address> doc = new Document<>(gramLength, curDocId++, node.getName(), node);
                    docs.add(doc);
                    for (String gram : doc.getGrams().keySet()) {
                        index.computeIfAbsent(gram, k -> new ArrayList<>()).add(doc);
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
                    new Searcher<>(streetIndex, docs, new AddressScoreCounter()));
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
                Document<AddressWordInfo> doc = new Document<>(gramLength, curDocId++, term, info);
                docs.add(doc);
                for (String gram : doc.getGrams().keySet()) {
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
