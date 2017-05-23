package ru.giss.search.parsing;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.giss.config.RootConfig;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RootConfig.class, TestConfig.class })
public class ParserTest {
    @Autowired
    private Parser parser;

    @Test
    public void test() {
        List<ParseResult> parseResults = parser.parse("сакнт питербург ленена");

        ParseResult mostRelevant = parseResults.get(0);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Санкт-Петербург"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Ленина"));
    }
}
