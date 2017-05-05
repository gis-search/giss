package ru.giss.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.giss.search.parsing.ParseResult;
import ru.giss.search.parsing.Parser;
import ru.giss.web.dto.SearchResult;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ApiController {

    private final Parser parser;

    @Autowired
    public ApiController(Parser parser) {
        this.parser = parser;
    }

    @GetMapping("/search")
    public List<SearchResult> search(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "debug", required = false) boolean debug) {
        List<ParseResult> results = parser.parse(text);
        return results.stream()
                .map(r -> new SearchResult(r, debug))
                .collect(Collectors.toList());
    }
}
