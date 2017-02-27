package ru.giss.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.giss.web.dto.SearchResult;

@RestController
public class GissRestController {
    @GetMapping("/search")
    public SearchResult search() {
        return new SearchResult(1L, "Санкт-Петербург");
    }
}
