package ru.giss.indexer;

import java.io.File;
import java.io.IOException;

/**
 * @author Ruslan Izmaylov
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String csvPath = Environment.getConfig().getProperty("giss.addresses.csv");
        String aliasPath = Environment.getConfig().getProperty("giss.aliases.csv");
        String protoPath = Environment.getConfig().getProperty("giss.addresses.proto");
        CsvToProtoConverter.convert(new File(csvPath), new File(aliasPath), new File(protoPath));
    }
}
