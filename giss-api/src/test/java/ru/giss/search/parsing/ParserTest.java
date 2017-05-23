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
    public void test1() {
        List<ParseResult> parseResults = parser.parse("сакнт питербург ленена");

        ParseResult mostRelevant = parseResults.get(0);
        ParseResult lessRelevant = parseResults.get(1);
        ParseResult lessRelevant2 = parseResults.get(2);
        ParseResult leastRelevant = parseResults.get(3);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Санкт-Петербург"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Ленина улица"));

        Assert.assertTrue(lessRelevant.getAddress().fullName().contains("Санкт-Петербург"));
        Assert.assertTrue(lessRelevant.getAddress().fullName().contains("Ленина площадь"));

        Assert.assertTrue(lessRelevant2.getAddress().fullName().contains("Санкт-Петербург"));
        Assert.assertTrue(lessRelevant2.getAddress().fullName().contains("площадь Ленина станция метро"));

        Assert.assertTrue(leastRelevant.getAddress().fullName().contains("Санкт-Петербург"));
        Assert.assertTrue(leastRelevant.getAddress().fullName().contains("Володарский"));
        Assert.assertTrue(leastRelevant.getAddress().fullName().contains("Ленина проспект"));
    }

    @Test
    public void test2() {
        List<ParseResult> parseResults = parser.parse("пл ленена сакнт питербург");

        ParseResult mostRelevant = parseResults.get(0);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Санкт-Петербург"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Ленина площадь"));
    }

    @Test
    public void test3() {
        List<ParseResult> parseResults = parser.parse("пл ленена сакнт питербург");

        ParseResult mostRelevant = parseResults.get(0);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Санкт-Петербург"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Ленина площадь"));
    }

    @Test
    public void test4() {
        List<ParseResult> parseResults = parser.parse("спб плтщдь ленена");

        ParseResult mostRelevant = parseResults.get(0);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Санкт-Петербург"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Ленина площадь"));
    }

    @Test
    public void test5() {
        List<ParseResult> parseResults = parser.parse("спб ст метро ленена");

        ParseResult mostRelevant = parseResults.get(0);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Санкт-Петербург"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Ленина станция метро"));
    }

    @Test
    public void test6() {
        List<ParseResult> parseResults = parser.parse("масква измайлвский");

        ParseResult mostRelevant = parseResults.get(0);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Москва"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Измайловский"));
    }

    @Test
    public void test7() {
        List<ParseResult> parseResults = parser.parse("масква измайлвский пр");

        ParseResult mostRelevant = parseResults.get(0);
        ParseResult leastRelevant = parseResults.get(1);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Москва"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Измайловский проспект"));

        Assert.assertTrue(leastRelevant.getAddress().fullName().contains("Москва"));
        Assert.assertTrue(leastRelevant.getAddress().fullName().contains("Измайловский проезд"));
    }

    @Test
    public void test8() {
        List<ParseResult> parseResults = parser.parse("масква измайлвский 18 1");

        ParseResult mostRelevant = parseResults.get(0);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Москва"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Измайловский проезд"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("18к1"));
    }

    @Test
    public void test9() {
        List<ParseResult> parseResults = parser.parse("мск измайлвский 18к1");

        ParseResult mostRelevant = parseResults.get(0);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Москва"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Измайловский проезд"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("18к1"));
    }

    @Test
    public void test10() {
        List<ParseResult> parseResults = parser.parse("масква измайлвский 63 корп 3");

        ParseResult mostRelevant = parseResults.get(0);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Москва"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Измайловский бульвар"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("63к3"));
    }

    //TODO THIS TEST SHOWS RESULT "Россия, Санкт-Петербург" - too many errors in request
    /*@Test
    public void test11() {
        List<ParseResult> parseResults = parser.parse("вонореж ленинмкий 104");

        ParseResult mostRelevant = parseResults.get(0);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Воронеж"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Ленинский проспект"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("104"));
    }*/

    //TODO FIX TEST for 18корп1
    /*@Test
    public void test12() {
        List<ParseResult> parseResults = parser.parse("18корп1 на измайлвскм мск");

        ParseResult mostRelevant = parseResults.get(0);

        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Москва"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("Измайловский проезд"));
        Assert.assertTrue(mostRelevant.getAddress().fullName().contains("18к1"));
    }*/
}
