package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class HabrCareerParseDetail {
    public static void main(String[] args) throws IOException {
        String link = "https://career.habr.com/vacancies/1000101566";
        HabrCareerParseDetail parseDetail = new HabrCareerParseDetail();
        System.out.println(parseDetail.retrieveDescription(link));
    }

    private String retrieveDescription(String link) throws IOException {
        Document document = Jsoup.connect(link).get();
        Element titleElement = document.selectFirst(".faded-content__container");
        return  titleElement.text();
    }
}
