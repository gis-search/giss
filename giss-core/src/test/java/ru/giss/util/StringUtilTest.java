package ru.giss.util;

import org.junit.Test;
import org.pcollections.PVector;
import ru.giss.util.model.token.Token;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class StringUtilTest {
    @Test
    public void biGrams() {
        Set<String> biGrams = StringUtil.nGramSet(2, "Москва");
        Set<String> expected = new HashSet<>();
        Collections.addAll(expected, "|М", "Мо", "ос", "ск", "кв", "ва");
        assertEquals(expected, biGrams);
    }

    @Test
    public void triGrams() {
        Set<String> triGrams = StringUtil.nGramSet(3, "Москва");
        Set<String> expected = new HashSet<>();
        Collections.addAll(expected, "||М", "|Мо", "Мос", "оск", "скв", "ква");
        assertEquals(expected, triGrams);
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
