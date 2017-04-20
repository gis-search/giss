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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    public Searcher getSearcher() throws IOException {
        LOG.info("Starting building indices...");

        try(InputStream is = new BufferedInputStream(new GZIPInputStream(new FileInputStream(gissFile)))) {
            Map<String, ArrayList<Address>> index = new HashMap<>();
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
                if (msg.getType() != AddressModel.AddressType.AT_CITY) continue;
                String[] grams = nGrams(gramLength, normalize(msg.getName(), false));
                for (String gram : grams) {
                    ArrayList<Address> optPosting = index.get(gram);
                    ArrayList<Address> posting = optPosting == null ? new ArrayList<>() : optPosting;
                    posting.add(node);
                    if (optPosting == null) {
                        index.put(gram, posting);
                    }
                }
            }

            LOG.info("Finished building indices");
            return new Searcher(index, nodes);
        }
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
