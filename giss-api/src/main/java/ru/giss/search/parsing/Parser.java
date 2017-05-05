package ru.giss.search.parsing;

import org.apache.commons.lang3.StringUtils;
import org.pcollections.PVector;
import ru.giss.AddressModel.AddressType;
import ru.giss.config.Backend;
import ru.giss.config.RootConfig;
import ru.giss.search.Match;
import ru.giss.search.Searcher;
import ru.giss.search.request.AddressSearchRequest;
import ru.giss.search.request.SearchRequest;
import ru.giss.util.model.address.Address;
import ru.giss.util.model.address.AddressWordInfo;
import ru.giss.util.model.address.HouseInfo;
import ru.giss.util.model.token.Token;

import java.util.*;
import java.util.regex.Matcher;
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
        Comparator<ParseContext> comparator =
                (o1, o2) -> Long.compare(o2.countScore(), o1.countScore());
        return Stream.of(new ParseContext(tokens))
                .map(this::parseAddressWords)
                .flatMap(this::parseRegion).distinct().sorted(comparator).limit(10)
                .flatMap(this::parseCity).distinct().sorted(comparator).limit(10)
                .flatMap(this::parseVillage).distinct().sorted(comparator).limit(10)
                .flatMap(this::parseStreet).distinct().sorted(comparator).limit(10)
                .map(this::parseHouse)
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

    private ParseContext parseHouse(ParseContext context) {
        Optional<ParseContext> optContextWithMatch = context.getOptStreet().flatMap(street -> {
            HouseInfo houseInfo = backend.getStreetToHouseInfo().get(street.getDoc());
            if (houseInfo == null) {
                return Optional.empty();
            }
            return context.findProbableHouseNumber().flatMap(tokenAndMatcher -> {
                Token token = tokenAndMatcher.getLeft();
                Matcher matcher = tokenAndMatcher.getRight();
                String number = matcher.group(RootConfig.NUMBER_REGEX_GROUP);
                String optBuilding = matcher.group(RootConfig.BUILDING_REGEX_GROUP);
                Token buildingToken = null;
                Map<String, Address> buildingToAddress = houseInfo.getNumberToBuildings().get(number);
                if (buildingToAddress == null) {
                    return Optional.empty();
                }
                if (optBuilding == null && buildingToAddress.size() > 1) {
                    PVector<Token> tokens = context.getTokens();
                    for (int i = token.getPosition() + 1; i < tokens.size(); i++) {
                        Token curToken = tokens.get(i);
                        if (StringUtils.isNumeric(curToken.getString())) {
                            optBuilding = curToken.getString();
                            buildingToken = curToken;
                            break;
                        }
                    }
                }
                Address resultAddress = buildingToAddress.get(optBuilding);
                if (resultAddress == null && optBuilding != null) {
                    resultAddress = buildingToAddress.get(null);
                }
                if (resultAddress == null) {
                    return Optional.empty();
                } else {
                    Match match = new Match<>(resultAddress, 10000000);
                    List<Token> matchTokens = new ArrayList<>(2);
                    matchTokens.add(token);
                    if (buildingToken != null) matchTokens.add(buildingToken);
                    return Optional.of(context.withAddress(match, matchTokens, AT_HOUSE));
                }
            });
        });
        return optContextWithMatch.orElse(context);
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
