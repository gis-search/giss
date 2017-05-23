package ru.giss.search.parsing;

import org.apache.commons.lang3.tuple.Pair;
import org.pcollections.PVector;
import ru.giss.AddressModel.AddressType;
import ru.giss.AddressModel.AddressWord;
import ru.giss.config.RootConfig;
import ru.giss.search.Match;
import ru.giss.util.model.address.Address;
import ru.giss.util.model.address.AddressWordInfo;
import ru.giss.util.model.token.AddressToken;
import ru.giss.util.model.token.AddressWordToken;
import ru.giss.util.model.token.Token;
import ru.giss.util.model.token.TokenType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author Ruslan Izmaylov
 */
public class ParseContext {

    private PVector<Token> tokens;
    private List<Match<AddressWordInfo>> addressWords = Collections.unmodifiableList(new ArrayList<>());
    private Optional<Match<Address>> optRegion = Optional.empty();
    private Optional<Match<Address>> optCity = Optional.empty();
    private Optional<Match<Address>> optVillage = Optional.empty();
    private Optional<Match<Address>> optStreet = Optional.empty();
    private Optional<Match<Address>> optHouse = Optional.empty();

    private Set<AddressWord> addrWordSet;

    public ParseContext(PVector<Token> tokens) {
        this.tokens = tokens;
    }

    public ParseContext(PVector<Token> tokens,
                        List<Match<AddressWordInfo>> addressWords,
                        Optional<Match<Address>> optRegion,
                        Optional<Match<Address>> optCity,
                        Optional<Match<Address>> optVillage,
                        Optional<Match<Address>> optStreet,
                        Optional<Match<Address>> optHouse) {
        this.tokens = tokens;
        this.addressWords = Collections.unmodifiableList(addressWords);
        this.optRegion = optRegion;
        this.optCity = optCity;
        this.optVillage = optVillage;
        this.optStreet = optStreet;
        this.optHouse = optHouse;

        addrWordSet = addressWords.stream().map(m -> m.getDoc().getWord()).collect(Collectors.toSet());

        if (optCity.isPresent() && optVillage.isPresent()) {
            throw new IllegalStateException("Parsed both city and village");
        }
    }

    public ParseContext withAddressWords(Map<Token, List<Match<AddressWordInfo>>> tokenToWords) {
        PVector<Token> newTokens = tokens;
        List<Match<AddressWordInfo>> newAddressWords = new ArrayList<>();
        for (Map.Entry<Token, List<Match<AddressWordInfo>>> kv : tokenToWords.entrySet()) {
            Token token = kv.getKey();
            List<Match<AddressWordInfo>> wordMatches = kv.getValue();
            List<AddressWord> words = new ArrayList<>(wordMatches.size());
            wordMatches.forEach(m -> words.add(m.getDoc().getWord()));
            TokenType type = new AddressWordToken(words);
            newTokens = newTokens.with(token.getPosition(), token.withType(type));
            newAddressWords.addAll(wordMatches);
        }
        return new ParseContext(newTokens, newAddressWords, optRegion, optCity, optVillage, optStreet, optHouse);
    }

    public ParseContext withAddress(Match<Address> addrMatch, List<Token> addrTokens, AddressType addrType) {
        PVector<Token> newTokens = tokens;
        for (Token t : addrTokens) {
            newTokens = newTokens.with(t.getPosition(), t.withType(new AddressToken(addrMatch.getDoc())));
        }
        switch (addrType) {
            case AT_REGION: return new ParseContext(newTokens, addressWords, Optional.of(addrMatch), optCity, optVillage, optStreet, optHouse);
            case AT_CITY: return new ParseContext(newTokens, addressWords, optRegion, Optional.of(addrMatch), optVillage, optStreet, optHouse);
            case AT_VILLAGE: return new ParseContext(newTokens, addressWords, optRegion, optCity, Optional.of(addrMatch), optStreet, optHouse);
            case AT_STREET: return new ParseContext(newTokens, addressWords, optRegion, optCity, optVillage, Optional.of(addrMatch), optHouse);
            case AT_HOUSE: return new ParseContext(newTokens, addressWords, optRegion, optCity, optVillage, optStreet, Optional.of(addrMatch));
            default: throw new IllegalArgumentException("Unsupported address type: " + addrType);
        }
    }

    public PVector<Token> getTokens() {
        return tokens;
    }

    public List<Token> getUnparsedTokens() {
        return tokens.stream().filter(Token::isUndefined).collect(Collectors.toList());
    }

    public List<Match<AddressWordInfo>> getAddressWords() {
        return addressWords;
    }

    public Set<AddressWord> getAddrWordSet() {
        return addrWordSet;
    }

    public Optional<Match<Address>> getOptCity() {
        return optCity;
    }

    public Optional<Match<Address>> getOptStreet() {
        return optStreet;
    }

    public Optional<Pair<Token, Matcher>> findProbableHouseNumber() {
        for (Token token : tokens) {
            if (token.isUndefined()) {
                Matcher matcher = RootConfig.HOUSE_REGEX.matcher(token.getString());
                if (matcher.find()) {
                    return Optional.of(Pair.of(token, matcher));
                }
            }
        }
        return Optional.empty();
    }

    public long countScore() {
        return optRegion.map(Match::getScore).orElse(0L) +
                optCity.map(Match::getScore).orElse(0L) +
                optVillage.map(Match::getScore).orElse(0L) +
                optStreet.map(Match::getScore).orElse(0L) +
                optHouse.map(Match::getScore).orElse(0L);
    }

    public Optional<Address> getNarrowestAddress() {
        return getNarrowestMatch().map(Match::getDoc);
    }

    public Optional<ParseResult> toResult() {
        Optional<Match<Address>> resultAddress = getNarrowestMatch();
        return resultAddress.map(m -> new ParseResult(m.getDoc(), countScore(), tokens));
    }

    private Optional<Match<Address>> getNarrowestMatch() {
        if (optHouse.isPresent()) return optHouse;
        if (optStreet.isPresent()) return optStreet;
        if (optCity.isPresent()) return optCity;
        if (optVillage.isPresent()) return optVillage;
        return optRegion;
    }
}
