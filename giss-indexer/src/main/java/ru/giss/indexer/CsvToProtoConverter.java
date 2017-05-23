package ru.giss.indexer;

import org.apache.commons.lang3.tuple.Pair;
import ru.giss.AddressModel;
import ru.giss.util.model.address.AddressWordUtil;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Ruslan Izmaylov
 */
public class CsvToProtoConverter {

    public static void convert(File csvFile, File aliasFile, File protoFile) throws IOException {
        Map<Integer, String[]> idToAliases = loadAliases(aliasFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(csvFile))));
        reader.readLine();
        OutputStream os = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(protoFile)));
        int lineNumber = -1;
        while (reader.ready()) {
            if (++lineNumber % 100000 == 0) System.out.println(lineNumber + " regions processed");
            String line = reader.readLine();
            String[] split = line.split(";");
            int id = Integer.parseInt(split[0]);
            int parentId = Integer.parseInt(split[1]);
            String country = split[2];
            String region = split[3];
            String area = split[4];
            String locality = split[5];
            String city = split[6];
            String village = split[7];
            String district = split[8];
            String street = split[9];
            String house = split[10];
            AddressModel.AddressType type = AddressModel.AddressType.valueOf(Integer.parseInt(split[11]));
            float lat = Float.parseFloat(split[12]);
            float lon = Float.parseFloat(split[13]);
            int childCount = Integer.parseInt(split[14]);
            Integer population = split.length >= 16 ? Integer.parseInt(split[15]) : null;

            AddressModel.AddressMsg.Builder builder = AddressModel.AddressMsg.newBuilder();
            builder.setId(id);
            builder.setParentId(parentId);
            String name;
            switch (type) {
                case AT_COUNTRY:
                    name = country; break;
                case AT_REGION:
                    name = region; break;
                case AT_AREA:
                    name = area; break;
                case AT_LOCALITY:
                    name = locality; break;
                case AT_CITY:
                    name = city; break;
                case AT_VILLAGE:
                    name = village; break;
                case AT_DISTRICT:
                    name = district; break;
                case AT_STREET:
                    name = street; break;
                case AT_HOUSE:
                    name = house; break;
                default:
                    throw new IllegalArgumentException("Unknown address type " + type);

            }
            Pair<String, List<AddressModel.AddressWordWithPosition>> nameAndAddressWords =
                    AddressWordUtil.stripAddressWords(name, type);
            builder.setName(nameAndAddressWords.getLeft());
            builder.addAllAddressWordWithPosition(nameAndAddressWords.getRight());
            String[] aliases = idToAliases.get(id);
            if (aliases != null) {
                builder.addAllSynonyms(Arrays.asList(aliases));
            }
            builder.setType(type);
            builder.setLatitude(lat);
            builder.setLongitude(lon);
            builder.setChildCount(childCount);
            if (population != null) builder.setPopulation(population);
            AddressModel.AddressMsg item = builder.build();
            item.writeDelimitedTo(os);
        }
        System.out.println("Converting successfully finished");
        os.close();
    }

    private static Map<Integer, String[]> loadAliases(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
        reader.readLine();
        Map<Integer, String[]> res = new HashMap<>();
        while (reader.ready()) {
            String line = reader.readLine();
            String[] split = line.split(";");
            int id = Integer.parseInt(split[0]);
            String[] aliases = split[1].split(",");
            res.put(id, aliases);
        }
        return res;
    }
}
