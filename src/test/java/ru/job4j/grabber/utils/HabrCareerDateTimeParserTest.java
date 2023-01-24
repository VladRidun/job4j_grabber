package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HabrCareerDateTimeParserTest {

    @Test
    void parseWhenTrue() {
        var date ="2023-01-24T08:51:58+03:00";
        LocalDateTime localDateTime = LocalDateTime.of(2023, Month.JANUARY,24,8,51,58);
        HabrCareerDateTimeParser habrTimeParser = new HabrCareerDateTimeParser();
        assertTrue(localDateTime.equals(habrTimeParser.parse(date)));
    }

    @Test
    void parseWhenFalse() {
        var date ="2023-01-24T08:51:58+03:00";
        LocalDateTime localDateTime = LocalDateTime.of(2023, Month.JANUARY,24,8,51);
        HabrCareerDateTimeParser habrTimeParser = new HabrCareerDateTimeParser();
        assertFalse(localDateTime.equals(habrTimeParser.parse(date)));
    }
}