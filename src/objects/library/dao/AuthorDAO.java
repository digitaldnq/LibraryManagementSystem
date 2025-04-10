package objects.library.dao;

import objects.library.model.Author;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {
    private Connection connection;

    public AuthorDAO(Connection connection) {
        this.connection = connection;
    }

    public void save(Author author) throws SQLException {
        String sql = "INSERT INTO authors (name) VALUES (?) RETURNING id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, author.getName());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int generatedId = rs.getInt("id");
                author.setId(generatedId);  // Вот тут мы возвращаем ID в объект
            }
        }
    }

    public List<Author> findAll() throws SQLException {
        List<Author> authors = new ArrayList<>();
        String query = "SELECT * FROM authors";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Author author = new Author(resultSet.getInt("id"), resultSet.getString("name"));
                authors.add(author);
            }
        }
        return authors;
    }

    public Author findById(int id) throws SQLException {
        String query = "SELECT * FROM authors WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return new Author(resultSet.getInt("id"), resultSet.getString("name"));
                }
            }
        }
        return null;
    }
}
