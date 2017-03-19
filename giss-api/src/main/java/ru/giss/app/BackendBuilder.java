package ru.giss.app;

import org.springframework.core.io.ClassPathResource;
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
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import static ru.giss.util.AddressMsgUtil.getName;
import static ru.giss.util.StringUtil.nGrams;
import static ru.giss.util.StringUtil.normalize;

/**
 * @author Ruslan Izmaylov
 */
public class BackendBuilder {

    private static Backend built;

    private Properties findProperties() throws IOException {
        ClassPathResource resource = new ClassPathResource("core.properties");
        Properties properties = new Properties();
        try (InputStream is = resource.getInputStream()) {
            properties.load(is);
        }
        return properties;
    }

    public Backend build() throws IOException {
        if (built != null) {
            return built;
        }
        Properties properties = findProperties();
        String gissFile = properties.getProperty("giss.addresses.proto");
        int gramLength = Integer.parseInt(properties.getProperty("giss.gram-length"));

        System.out.println("Starting building indices...");
        InputStream is = new BufferedInputStream(new GZIPInputStream(new FileInputStream(gissFile)));
        Map<String, ArrayList<Address>> index = new HashMap<>();
        AddressModel.AddressMsg msg;
        ArrayList<Address> nodes = new ArrayList<>(6700000);
        nodes.add(null);
        while ((msg = AddressModel.AddressMsg.parseDelimitedFrom(is)) != null) {
            Address node = new Address(
                    msg.getId(),
                    nodes.get(msg.getParentId()),
                    getName(msg),
                    msg.getType(),
                    msg.getLatitude(),
                    msg.getLongitude(),
                    msg.getChildCount(),
                    msg.getPopulation());
            nodes.add(node);
            if (msg.getType() != AddressModel.AddressType.CITY) continue;
            String[] grams = nGrams(gramLength, normalize(getName(msg)));
            for (String gram : grams) {
                ArrayList<Address> optPosting = index.get(gram);
                ArrayList<Address> posting = optPosting == null ? new ArrayList<>() : optPosting;
                posting.add(node);
                if (optPosting == null) {
                    index.put(gram, posting);
                }
            }
        }
        is.close();
        System.out.println("Finished building indices");
        Searcher searcher = new Searcher(index, nodes);
        built = new Backend(searcher);
        return built;
    }
}
