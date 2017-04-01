package ru.giss.util.address;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import ru.giss.AddressModel.AddressType;
import ru.giss.AddressModel.AddressWordWithPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.giss.util.StringUtil.normalize;

/**
 * @author Ruslan Izmaylov
 */
public class AddressWordUtil {

    /**
     * Removes words that denote address type.
     *
     * Returns resulting string and removed words
     * with information bout their position. This information
     * is used when composing full address for search result rendering.
     */
    public static Pair<String, List<AddressWordWithPosition>> stripAddressWords(String s, AddressType addressType) {
        Trie trie = IndexedAddressedWords.getTrie();
        String normalized = normalize(s, true);
        Collection<Emit> emits = trie.parseText(normalized);
        List<AddressWordWithPosition> resultWords = new ArrayList<>();
        String resultString = s;
        for (Emit emit : emits) {
            List<AddressWordInfo> wordInfos = IndexedAddressedWords.getNameToAddressWordInfos().get(emit.getKeyword());
            Optional<AddressWordInfo> optWordInfo = wordInfos.stream().filter(wi -> wi.getAddressType() == addressType).findFirst();
            if (optWordInfo.isPresent()) {
                AddressWordWithPosition.Builder wordBuilder = AddressWordWithPosition.newBuilder();
                wordBuilder.setWord(optWordInfo.get().getWord());
                wordBuilder.setIsPrefix(s.length() - emit.getEnd() > 2);
                resultWords.add(wordBuilder.build());

                resultString = resultString.substring(0, emit.getStart()) +
                        StringUtils.repeat(' ', emit.size());
                if (emit.getEnd() < s.length() - 1) {
                    resultString += s.substring(emit.getEnd() + 1);
                }
            }
        }
        resultString = resultString.replaceAll(" \\.", " ");
        resultString = resultString.trim().replaceAll("  ", " ");
        return Pair.of(resultString, resultWords);
    }
}
