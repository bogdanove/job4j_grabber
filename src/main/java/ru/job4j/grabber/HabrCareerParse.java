package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(String.format(link));
        Document document = connection.get();
        Elements rows = document.select(".collapsible-description__content");
        return rows.get(0).child(0).text();
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            Connection connection = Jsoup.connect(String.format("%s?page=%s", link, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String href = String.format("%s%s", link, linkElement.attr("href"));
                Element dateElement = row.select(".vacancy-card__date").first().child(0);
                String dateVacancy = dateElement.attr("datetime");
                try {
                    posts.add(new Post(vacancyName, href, retrieveDescription(href), dateTimeParser.parse(dateVacancy)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return posts;
    }
}
