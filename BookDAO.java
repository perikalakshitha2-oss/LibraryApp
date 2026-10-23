package com.library.dao;

import com.library.model.Book;
import com.library.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-Access Object for the {@code books} table.
 * Every method opens/uses the shared connection and handles its own
 * PreparedStatement lifecycle.
 */
public class BookDAO {

    // ── CREATE ───────────────────────────────────────────────

    public boolean addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, author, publisher, " +
                     "publication_year, genre, total_copies, available_copies) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getPublisher());
            ps.setInt   (5, book.getPublicationYear());
            ps.setString(6, book.getGenre());
            ps.setInt   (7, book.getTotalCopies());
            ps.setInt   (8, book.getAvailableCopies());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) book.setBookId(keys.getInt(1));
                return true;
            }
            return false;
        }
    }

    // ── READ ─────────────────────────────────────────────────

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";

        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) books.add(mapRow(rs));
        }
        return books;
    }

    public List<Book> getAvailableBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE available_copies > 0 ORDER BY title";

        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) books.add(mapRow(rs));
        }
        return books;
    }

    public Book getBookById(int bookId) throws SQLException {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    public List<Book> searchBooks(String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? OR genre LIKE ?";
        String kw  = "%" + keyword + "%";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
        }
        return books;
    }

    // ── UPDATE ───────────────────────────────────────────────

    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET isbn=?, title=?, author=?, publisher=?, " +
                     "publication_year=?, genre=?, total_copies=?, available_copies=? " +
                     "WHERE book_id=?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getPublisher());
            ps.setInt   (5, book.getPublicationYear());
            ps.setString(6, book.getGenre());
            ps.setInt   (7, book.getTotalCopies());
            ps.setInt   (8, book.getAvailableCopies());
            ps.setInt   (9, book.getBookId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Atomically decrement available_copies by 1 (borrow). */
    public boolean decrementAvailableCopies(int bookId) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies - 1 " +
                     "WHERE book_id = ? AND available_copies > 0";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Atomically increment available_copies by 1 (return). */
    public boolean incrementAvailableCopies(int bookId) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies + 1 " +
                     "WHERE book_id = ? AND available_copies < total_copies";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ───────────────────────────────────────────────

    public boolean deleteBook(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Private helper ───────────────────────────────────────

    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setBookId         (rs.getInt   ("book_id"));
        b.setIsbn           (rs.getString("isbn"));
        b.setTitle          (rs.getString("title"));
        b.setAuthor         (rs.getString("author"));
        b.setPublisher      (rs.getString("publisher"));
        b.setPublicationYear(rs.getInt   ("publication_year"));
        b.setGenre          (rs.getString("genre"));
        b.setTotalCopies    (rs.getInt   ("total_copies"));
        b.setAvailableCopies(rs.getInt   ("available_copies"));
        return b;
    }
}
