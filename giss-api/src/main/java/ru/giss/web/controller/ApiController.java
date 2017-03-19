package ru.giss.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.giss.app.BackendBuilder;
import ru.giss.model.Address;
import ru.giss.search.Match;
import ru.giss.search.SearchRequest;
import ru.giss.search.Searcher;
import ru.giss.web.dto.SearchResult;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class ApiController {

    // TODO inject properly
    private final Searcher citySearcher;

    public ApiController() throws IOException {
        citySearcher = new BackendBuilder().build().getCitySearcher();
    }

    @GetMapping("/search")
    public SearchResult search(
            @RequestParam(value = "text") String text) {
        ArrayList<Match> results = citySearcher.search(new SearchRequest(text));
        if (results.isEmpty()) {
            return null;
        } else {
            Address top = results.get(0).getDoc();
            return new SearchResult(top);
        }
    }
}
