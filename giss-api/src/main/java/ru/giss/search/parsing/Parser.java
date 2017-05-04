package ru.giss.search.parsing;

import org.pcollections.PVector;
import ru.giss.AddressModel.AddressType;
import ru.giss.config.Backend;
import ru.giss.search.Match;
import ru.giss.search.Searcher;
import ru.giss.search.request.AddressSearchRequest;
import ru.giss.search.request.SearchRequest;
import ru.giss.util.model.address.Address;
import ru.giss.util.model.address.AddressWordInfo;
import ru.giss.util.model.token.Token;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.giss.AddressModel.AddressType.*;
import static ru.giss.util.StringUtil.*;

/**
 * @author Ruslan Izmaylov
 */
public class Parser {

    private final Backend backend;
    private final int gramLength;

    public Parser(Backend backend) {
        this.backend = backend;
        this.gramLength = backend.getGramLength();
    }

    public List<ParseResult> parse(String text) {
        String normalized = normalize(text, true);
        PVector<Token> tokens = tokenize(normalized);
        return Stream.of(new ParseContext(tokens))
                .map(this::parseAddressWords)
                .flatMap(this::parseRegion)
                .flatMap(this::parseCity)
                .flatMap(this::parseVillage)
                .flatMap(this::parseStreet)
                .map(ParseContext::toResult)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .sorted((o1, o2) -> Long.compare(o2.getScore(), o1.getScore()))
                .limit(10)
                .collect(Collectors.toList());
    }

    private ParseContext parseAddressWords(ParseContext context) {
        List<Token> tokens = context.getUnparsedTokens();
        Map<Token, List<Match<AddressWordInfo>>> tokenToWords = new HashMap<>();
        for (int beginToken = 0; beginToken < tokens.size(); beginToken++) {
            ArrayList<Match<AddressWordInfo>> parsed = new ArrayList<>(0);
            int bestEndToken = -1;
            for (int endToken = beginToken + 1; endToken <= tokens.size() && endToken < beginToken + 4; endToken++) {
                String qString = join(tokens, beginToken, endToken);
                SearchRequest req = new SearchRequest(qString, gramLength);
                ArrayList<Match<AddressWordInfo>> res = backend.getAddrWordSearcher().search(req);
                if (!res.isEmpty()) {
                    parsed = res;
                    bestEndToken = endToken;
                }
            }
            if (!parsed.isEmpty()) {
                for (int i = beginToken; i < bestEndToken; i++) {
                    tokenToWords.put(tokens.get(i), parsed);
                }
            }
        }
        return context.withAddressWords(tokenToWords);
    }

    private Stream<ParseContext> parseRegion(ParseContext context) {
        if (context.getAddressWords().stream().noneMatch(m -> m.getDoc().getAddressType() == AT_REGION)) {
            return Stream.of(context);
        }
        List<ParseContext> res = parseAddress(context, AT_REGION, backend.getRegionSearcher());
        if (res.isEmpty()) {
            return Stream.of(context);
        } else {
            return res.stream();
        }
    }

    private Stream<ParseContext> parseCity(ParseContext context) {
        List<ParseContext> res = parseAddress(context, AT_CITY, backend.getCitySearcher());
        if (res.isEmpty()) {
            return Stream.of(context);
        } else {
            return res.stream();
        }
    }

    private Stream<ParseContext> parseVillage(ParseContext context) {
        if (context.getOptCity().isPresent()) {
            return Stream.of(context);
        }
        List<ParseContext> res = parseAddress(context, AT_VILLAGE, backend.getVillageSearcher());
        if (res.isEmpty()) {
            return Stream.of(context);
        } else {
            return res.stream();
        }
    }

    private Stream<ParseContext> parseStreet(ParseContext context) {
        List<ParseContext> res = parseAddress(context, AT_STREET, backend.getStreetSearcher());
        if (res.isEmpty()) {
            return Stream.of(context);
        } else {
            return res.stream();
        }
    }

    private List<ParseContext> parseAddress(ParseContext context,
                                            AddressType type,
                                            Searcher<Address, AddressSearchRequest> searcher) {
        List<Token> tokens = context.getUnparsedTokens();
        Optional<Address> optParent = context.getNarrowestAddress();
        Map<Match<Address>, List<Token>> addressToTokens = new HashMap<>();
        for (int beginToken = 0; beginToken < tokens.size(); beginToken++) {
            ArrayList<Match<Address>> parsed = new ArrayList<>(0);
            int bestEndToken = -1;
            for (int endToken = beginToken + 1; endToken <= tokens.size() && endToken < beginToken + 4; endToken++) {
                String qString = join(tokens, beginToken, endToken);
                AddressSearchRequest req = new AddressSearchRequest(qString, gramLength, context.getAddrWordSet(), optParent);
                ArrayList<Match<Address>> res = searcher.search(req);
                if (!res.isEmpty()) {
                    parsed = res;
                    bestEndToken = endToken;
                }
            }
            for (Match<Address> addrMatch : parsed) {
                List<Token> curTokens = addressToTokens.computeIfAbsent(addrMatch, k -> new ArrayList<>());
                curTokens.addAll(tokens.subList(beginToken, bestEndToken));
            }
        }
        List<ParseContext> res = new ArrayList<>();
        for (Map.Entry<Match<Address>, List<Token>> kv : addressToTokens.entrySet()) {
            res.add(context.withAddress(kv.getKey(), kv.getValue(), type));
        }
        return res;
    }
}
