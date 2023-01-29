package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String VACANCY_LINK = "/vacancies/java_developer";
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private String retrieveDescription(String link) {
        try {
            return Jsoup.connect(link).get().selectFirst(".vacancy-description__text").text();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> postList = new ArrayList<>();
        AtomicInteger id = new AtomicInteger();
        for (int i = 1; i <= 5; i++) {
            var pageLink = String.format("%s?page=%s", link, i);
            Elements rows = Jsoup.connect(pageLink).get().select(".vacancy-card__inner");
            rows.forEach(row -> {
                var datetime = dateTimeParser.parse(row.select(".vacancy-card__date").first().child(0).attr("datetime"));
                var vacancyName = row.select(".vacancy-card__title").first().text();
                var linkPost = String.format("%s%s", SOURCE_LINK, row.select(".vacancy-card__title").first().child(0).attr("href"));
                postList.add(new Post(id.incrementAndGet(),
                        vacancyName,
                        linkPost,
                        retrieveDescription(linkPost),
                        datetime));
            });
        }
        return postList;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerDateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(dateTimeParser);
        String link = String.format("%s%s", SOURCE_LINK, VACANCY_LINK);
        habrCareerParse.list(link).forEach(System.out::println);
    }
}