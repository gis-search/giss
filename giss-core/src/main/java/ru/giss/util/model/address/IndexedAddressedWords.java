package ru.giss.util.model.address;

import org.ahocorasick.trie.Trie;
import ru.giss.AddressModel.AddressWord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.giss.AddressModel.AddressType.*;
import static ru.giss.AddressModel.AddressWord.*;

/**
 * @author Ruslan Izmaylov
 */
public class IndexedAddressedWords {

    private static Trie trie;
    private static Map<String, List<AddressWordInfo>> nameToAddressWordInfos;
    private static Map<AddressWord, AddressWordInfo> addressWordToInfo;

    static  {
        List<AddressWordInfo> infos = new ArrayList<>();
        infos.add(new AddressWordInfo(AW_ULITSA, "улица", new String[] { "ул" }, AT_STREET));
        infos.add(new AddressWordInfo(AW_PROSPEKT, "проспект", new String[] { "пр" }, AT_STREET));
        infos.add(new AddressWordInfo(AW_PLOSCHAD, "площадь", new String[] { "пл" }, AT_STREET));
        infos.add(new AddressWordInfo(AW_PEREULOK, "переулок", new String[] { "пер" }, AT_STREET));
        infos.add(new AddressWordInfo(AW_METRO, "станция метро", new String[] { "ст м", "метро"}, AT_STREET));
        infos.add(new AddressWordInfo(AW_NABEREZHNAYA, "набережная", new String[] { "наб" }, AT_STREET));
        infos.add(new AddressWordInfo(AW_LINIYA, "линия", new String[] { "л" }, AT_STREET));
        infos.add(new AddressWordInfo(AW_SHOSSE, "шоссе", new String[] { "ш" }, AT_STREET));
        infos.add(new AddressWordInfo(AW_ALLEYA, "аллея", new String[] { "ал" }, AT_STREET));
        infos.add(new AddressWordInfo(AW_OBLAST, "область", new String[] { "обл" }, AT_REGION));
        infos.add(new AddressWordInfo(AW_KRAY, "край", new String[] { "кр" }, AT_REGION));
        infos.add(new AddressWordInfo(AW_RAYON, "район", new String[] { "р н" }, AT_AREA));
        infos.add(new AddressWordInfo(AW_GOROD, "город", new String[] { "гор", "г" }, AT_CITY));
        infos.add(new AddressWordInfo(AW_RESPUBLIKA, "республика", new String[] { "респ" }, AT_REGION));
        infos.add(new AddressWordInfo(AW_AVTONOMNY_OKRUG, "автономный округ", new String[] { "ао", "а о", "автономная область" }, AT_REGION));
        infos.add(new AddressWordInfo(AW_BULVAR, "бульвар", new String[] { "бульвар" }, AT_STREET));
        infos.add(new AddressWordInfo(AW_PROEZD, "проезд", new String[] { "пр" }, AT_STREET));

        nameToAddressWordInfos = new HashMap<>();
        addressWordToInfo = new HashMap<>();
        Trie.TrieBuilder trieBuilder = Trie.builder().removeOverlaps().onlyWholeWordsWhiteSpaceSeparated();
        for (AddressWordInfo info : infos) {
            addressWordToInfo.put(info.getWord(), info);
            nameToAddressWordInfos.computeIfAbsent(info.getName(), k -> new ArrayList<>()).add(info);
            trieBuilder.addKeyword(info.getName());
            for (String syn : info.getSynonyms()) {
                nameToAddressWordInfos.computeIfAbsent(syn, k -> new ArrayList<>()).add(info);
                trieBuilder.addKeyword(syn);
            }
        }
        trie = trieBuilder.build();
    }

    public static Trie getTrie() {
        return trie;
    }

    public static Map<String, List<AddressWordInfo>> getNameToAddressWordInfos() {
        return nameToAddressWordInfos;
    }

    public static Map<AddressWord, AddressWordInfo> getAddressWordToInfo() {
        return addressWordToInfo;
    }
}
