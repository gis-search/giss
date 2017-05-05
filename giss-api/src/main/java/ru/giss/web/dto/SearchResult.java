package ru.giss.web.dto;

import ru.giss.search.parsing.ParseResult;
import ru.giss.util.model.token.Token;

import java.util.List;
import java.util.stream.Collectors;

public class SearchResult {

    private ParseResult parseResult;
    private boolean debug;

    public SearchResult(ParseResult parseResult, boolean debug) {
        this.parseResult = parseResult;
        this.debug = debug;
    }

    public String getAddress() {
        return parseResult.getAddress().fullName();
    }

    public float getLat() {
        return parseResult.getAddress().getLat();
    }

    public float getLon() {
        return parseResult.getAddress().getLon();
    }

    public List<String> getDebug() {
        if (!debug) return null;
        return parseResult.getTokens().stream().map(Token::toString).collect(Collectors.toList());
    }
}
