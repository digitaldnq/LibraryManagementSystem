package objects.library.dao;

import objects.library.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private Connection connection;

    public BookDAO(Connection connection) {
        this.connection = connection;
    }

    public void save(Book book) throws SQLException {
        String query = "INSERT INTO books (title, author_id, available) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, book.getTitle());
            ps.setInt(2, book.getAuthor().getId()); // Используем ID автора
            ps.setBoolean(3, book.isAvailable());
            ps.executeUpdate();
        }
    }

    public List<Book> findAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int authorId = resultSet.getInt("author_id");
                Author author = new AuthorDAO(connection).findById(authorId); // Получаем автора по ID
                Book book = new Book(resultSet.getInt("id"), resultSet.getString("title"), author, resultSet.getBoolean("available"));
                books.add(book);
            }
        }
        return books;
    }

    public Book findById(int id) throws SQLException {
        String query = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    int authorId = resultSet.getInt("author_id");
                    Author author = new AuthorDAO(connection).findById(authorId); // Получаем автора по ID
                    return new Book(resultSet.getInt("id"), resultSet.getString("title"), author, resultSet.getBoolean("available"));
                }
            }
        }
        return null;
    }

    public void update(Book book) throws SQLException {
        String query = "UPDATE books SET title = ?, author_id = ?, available = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, book.getTitle());
            ps.setInt(2, book.getAuthor().getId()); // Используем ID автора
            ps.setBoolean(3, book.isAvailable());
            ps.setInt(4, book.getId());
            ps.executeUpdate();
        }
    }
}
