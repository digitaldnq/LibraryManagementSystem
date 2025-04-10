package objects.library.dao;

import objects.library.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO {
    private Connection connection;

    public BorrowingDAO(Connection connection) {
        this.connection = connection;
    }

    public void save(Borrowing borrowing) throws SQLException {
        String query = "INSERT INTO borrowings (user_id, book_id, borrow_date, return_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, borrowing.getUser().getId());
            ps.setInt(2, borrowing.getBook().getId());
            ps.setDate(3, Date.valueOf(borrowing.getBorrowDate()));
            ps.setDate(4, borrowing.getReturnDate() != null ? Date.valueOf(borrowing.getReturnDate()) : null);
            ps.executeUpdate();
        }
    }

    public List<Borrowing> findAll() throws SQLException {
        List<Borrowing> borrowings = new ArrayList<>();
        String query = "SELECT * FROM borrowings";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                int bookId = resultSet.getInt("book_id");
                User user = new UserDAO(connection).findById(userId);
                Book book = new BookDAO(connection).findById(bookId);
                Borrowing borrowing = new Borrowing(resultSet.getInt("id"), user, book,
                        resultSet.getDate("borrow_date").toLocalDate(),
                        resultSet.getDate("return_date") != null ? resultSet.getDate("return_date").toLocalDate() : null);
                borrowings.add(borrowing);
            }
        }
        return borrowings;
    }

    public void updateReturnDate(int borrowingId, LocalDate returnDate) throws SQLException {
        String query = "UPDATE borrowings SET return_date = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(returnDate));
            ps.setInt(2, borrowingId);
            ps.executeUpdate();
        }
    }

    public Borrowing findById(int id) throws SQLException {
        String query = "SELECT b.id, b.borrow_date, b.return_date, " +
                "u.id as user_id, u.name as user_name, " +
                "bk.id as book_id, bk.title, bk.available, a.id as author_id, a.name as author_name " +
                "FROM borrowings b " +
                "JOIN users u ON b.user_id = u.id " +
                "JOIN books bk ON b.book_id = bk.id " +
                "JOIN authors a ON bk.author_id = a.id " +
                "WHERE b.id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User(rs.getInt("user_id"), rs.getString("user_name"));
                    Author author = new Author(rs.getInt("author_id"), rs.getString("author_name"));
                    Book book = new Book(rs.getInt("book_id"), rs.getString("title"), author, rs.getBoolean("available"));
                    return new Borrowing(rs.getInt("id"), user, book,
                            rs.getDate("borrow_date").toLocalDate(),
                            rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null);
                }
            }
        }
        return null;
    }
}
