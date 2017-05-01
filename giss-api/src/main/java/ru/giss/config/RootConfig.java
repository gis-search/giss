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
import ru.giss.model.Address;
import ru.giss.search.Searcher;
import ru.giss.search.score.SimpleAddressScoreCounter;
import ru.giss.search.score.SimpleAddressWordScoreCounter;
import ru.giss.util.StringUtil;
import ru.giss.util.address.AddressWordInfo;
import ru.giss.util.address.IndexedAddressedWords;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static ru.giss.util.StringUtil.nGrams;
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

    @Bean
    public Backend getBackend() throws IOException {
        LOG.info("Starting building indices...");

        try(InputStream is = new BufferedInputStream(new GZIPInputStream(new FileInputStream(gissFile)))) {
            Map<String, ArrayList<Address>> regionIndex = new HashMap<>();
            Map<String, ArrayList<Address>> cityIndex = new HashMap<>();
            Map<String, ArrayList<Address>> villageIndex = new HashMap<>();
            Map<String, ArrayList<Address>> streetIndex = new HashMap<>();
            AddressModel.AddressMsg msg;
            ArrayList<Address> nodes = new ArrayList<>(6700000);
            nodes.add(null);
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
                Map<String, ArrayList<Address>> index;
                switch (msg.getType()) {
                    case AT_REGION: index = regionIndex; break;
                    case AT_CITY: index = cityIndex; break;
                    case AT_VILLAGE: index = villageIndex; break;
                    case AT_STREET: index = streetIndex; break;
                    default: continue;
                }
                String[] grams = nGrams(gramLength, normalize(msg.getName(), false));
                for (String gram : grams) {
                    index.computeIfAbsent(gram, k -> new ArrayList<>()).add(node);
                }
            }
            Searcher<AddressWordInfo> addrWordSearcher = indexAddrWords();
            LOG.info("Finished building indices");

            return new Backend(
                    addrWordSearcher,
                    new Searcher<>(regionIndex, nodes, new SimpleAddressScoreCounter()),
                    new Searcher<>(cityIndex, nodes, new SimpleAddressScoreCounter()),
                    new Searcher<>(villageIndex, nodes, new SimpleAddressScoreCounter()),
                    new Searcher<>(streetIndex, nodes, new SimpleAddressScoreCounter()));
        }
    }

    private Searcher<AddressWordInfo> indexAddrWords() {
        Map<String, ArrayList<AddressWordInfo>> index = new HashMap<>();
        Set<AddressWordInfo> addrWords = new TreeSet<>(Comparator.comparingInt(AddressWordInfo::getId));
        addrWords.addAll(IndexedAddressedWords.getAddressWordToInfo().values());
        ArrayList<AddressWordInfo> docs = new ArrayList<>();
        docs.add(null);
        for (AddressWordInfo info : addrWords) {
            Set<String> nGrams = new HashSet<>();
            Collections.addAll(nGrams, StringUtil.nGrams(gramLength, info.getName()));
            for (String syn : info.getSynonyms()) {
                Collections.addAll(nGrams, StringUtil.nGrams(gramLength, syn));
            }
            for (String nGram : nGrams) {
                index.computeIfAbsent(nGram, k -> new ArrayList<>()).add(info);
            }
            docs.add(info);
        }
        return new Searcher<>(index, docs, new SimpleAddressWordScoreCounter());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
