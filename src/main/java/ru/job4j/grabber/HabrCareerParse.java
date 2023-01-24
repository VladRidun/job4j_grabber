package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String VACANCY_LINK = "/vacancies/java_developer";

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Страница " + i);
            String pageLink = String.format("%s%s?page=%s", SOURCE_LINK, VACANCY_LINK, i);
            Connection connection = Jsoup.connect(pageLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first();
                Element date = dateElement.child(0);
                String vacancyName = titleElement.text();
                HabrCareerDateTimeParser feedparser = new HabrCareerDateTimeParser();
                LocalDateTime dateStr = (feedparser.parse(date.attr("datetime")));
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%s %s %s%n", vacancyName, link, dateStr);
            });
        }
    }
}