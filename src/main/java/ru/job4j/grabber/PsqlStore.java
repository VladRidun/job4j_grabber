package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {
    private static final String SQL_INSERT = "insert into post(name, text, link, created) values (?, ?, ?, ?) ON CONFLICT (link) DO NOTHING";
    private static final String SQL_FIND_ALL = "select * from post";
    private static final String SQL_FIND_BY_ID = "select * from post where id = ?";

    private Connection cn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            cn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     cn.prepareStatement(SQL_INSERT,
                             Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = cn.prepareStatement(SQL_FIND_ALL)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(getPostFromResultSet(resultSet));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement = cn.prepareStatement(SQL_FIND_BY_ID)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post = getPostFromResultSet(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    private Post getPostFromResultSet(ResultSet resultSet) throws SQLException {
        return new Post(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("link"),
                resultSet.getString("text"),
                resultSet.getTimestamp("created").toLocalDateTime());
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }

    public static void main(String[] args) {
        Properties cfg = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("app.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Post post1 = new Post("Java-разработчик / Java-developer", "https://career.habr.com/vacancies/1000109498", "«Передовые Платежные Решения»", LocalDateTime.now());
        Post post2 = new Post("Java программист", "https://career.habr.com/vacancies/1000101566", "Sportmaster Lab", LocalDateTime.now());
        Post post3 = new Post("Java Developer", "https://career.habr.com/vacancies/1000119017", "Газпромбанк", LocalDateTime.now());
        try (PsqlStore psqlStore = new PsqlStore(cfg)) {
            psqlStore.save(post1);
            psqlStore.save(post2);
            psqlStore.save(post3);
            System.out.println("Выполнение поиска по индексу 3");
            System.out.println(psqlStore.findById(3));
            System.out.println("Выполнение поиска всех записей в таблице");
            psqlStore.getAll().forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
