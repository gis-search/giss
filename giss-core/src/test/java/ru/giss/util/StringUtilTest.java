package ru.giss.util;

import org.junit.Test;
import org.pcollections.PVector;
import ru.giss.util.model.token.Token;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class StringUtilTest {
    @Test
    public void biGrams() {
        String[] biGrams = StringUtil.nGrams(2, "Москва");

        assertArrayEquals(
                new String[]{"|М", "Мо", "ос", "ск", "кв", "ва"},
                biGrams
        );
    }

    @Test
    public void triGrams() {
        String[] triGrams = StringUtil.nGrams(3, "Москва");

        assertArrayEquals(
                new String[]{"||М", "|Мо", "Мос", "оск", "скв", "ква"},
                triGrams
        );
    }

    @Test
    public void tokenization() {
        PVector<Token> tokens = StringUtil.tokenize("санкт- петербург\t 23 ");
        assertEquals("санкт", tokens.get(0).getString());
        assertEquals("петербург", tokens.get(1).getString());
        assertEquals("23", tokens.get(2).getString());
        assertEquals(0, tokens.get(0).getPosition());
        assertEquals(1, tokens.get(1).getPosition());
        assertEquals(2, tokens.get(2).getPosition());
        assertEquals(true, tokens.stream().allMatch(Token::isUndefined));
    }

    @Test
    public void joining() {

        PVector<Token> tokens = StringUtil.tokenize("санкт петербург улица пушкина");
        String joined = StringUtil.join(tokens, 0, 2);
        assertEquals("санкт петербург", joined);
    }
}
