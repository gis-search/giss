package ru.giss.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

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
}
