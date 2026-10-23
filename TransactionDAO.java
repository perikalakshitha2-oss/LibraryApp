package com.library.dao;

import com.library.model.Transaction;
import com.library.model.Transaction.Status;
import com.library.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-Access Object for the {@code transactions} table.
 */
public class TransactionDAO {

    // ── CREATE ───────────────────────────────────────────────

    public boolean addTransaction(Transaction tx) throws SQLException {
        String sql = "INSERT INTO transactions (member_id, book_id, borrow_date, due_date, status) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt   (1, tx.getMemberId());
            ps.setInt   (2, tx.getBookId());
            ps.setDate  (3, Date.valueOf(tx.getBorrowDate()));
            ps.setDate  (4, Date.valueOf(tx.getDueDate()));
            ps.setString(5, Status.BORROWED.name());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) tx.setTransactionId(keys.getInt(1));
                return true;
            }
            return false;
        }
    }

    // ── READ ─────────────────────────────────────────────────

    /** Full list with member name & book title joined. */
    public List<Transaction> getAllTransactions() throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.*, m.name AS member_name, b.title AS book_title " +
                     "FROM transactions t " +
                     "JOIN members m ON t.member_id = m.member_id " +
                     "JOIN books   b ON t.book_id   = b.book_id " +
                     "ORDER BY t.transaction_id DESC";

        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Transaction> getActiveTransactions() throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.*, m.name AS member_name, b.title AS book_title " +
                     "FROM transactions t " +
                     "JOIN members m ON t.member_id = m.member_id " +
                     "JOIN books   b ON t.book_id   = b.book_id " +
                     "WHERE t.status IN ('BORROWED','OVERDUE') " +
                     "ORDER BY t.due_date";

        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Transaction> getTransactionsByMember(int memberId) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.*, m.name AS member_name, b.title AS book_title " +
                     "FROM transactions t " +
                     "JOIN members m ON t.member_id = m.member_id " +
                     "JOIN books   b ON t.book_id   = b.book_id " +
                     "WHERE t.member_id = ? ORDER BY t.borrow_date DESC";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Transaction getTransactionById(int txId) throws SQLException {
        String sql = "SELECT t.*, m.name AS member_name, b.title AS book_title " +
                     "FROM transactions t " +
                     "JOIN members m ON t.member_id = m.member_id " +
                     "JOIN books   b ON t.book_id   = b.book_id " +
                     "WHERE t.transaction_id = ?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, txId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    // ── UPDATE ───────────────────────────────────────────────

    public boolean returnBook(Transaction tx) throws SQLException {
        String sql = "UPDATE transactions SET return_date=?, fine_amount=?, status=? " +
                     "WHERE transaction_id=?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDate  (1, Date.valueOf(tx.getReturnDate()));
            ps.setBigDecimal(2, tx.getFineAmount());
            ps.setString(3, Status.RETURNED.name());
            ps.setInt   (4, tx.getTransactionId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Mark any BORROWED transactions past their due date as OVERDUE. */
    public int markOverdueTransactions() throws SQLException {
        String sql = "UPDATE transactions SET status = 'OVERDUE' " +
                     "WHERE status = 'BORROWED' AND due_date < CURDATE()";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            return st.executeUpdate(sql);
        }
    }

    // ── Private helper ───────────────────────────────────────

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction tx = new Transaction();
        tx.setTransactionId(rs.getInt   ("transaction_id"));
        tx.setMemberId     (rs.getInt   ("member_id"));
        tx.setMemberName   (rs.getString("member_name"));
        tx.setBookId       (rs.getInt   ("book_id"));
        tx.setBookTitle    (rs.getString("book_title"));
        tx.setBorrowDate   (rs.getDate  ("borrow_date").toLocalDate());
        tx.setDueDate      (rs.getDate  ("due_date").toLocalDate());

        Date ret = rs.getDate("return_date");
        if (ret != null) tx.setReturnDate(ret.toLocalDate());

        tx.setFineAmount(rs.getBigDecimal("fine_amount"));
        tx.setStatus    (Status.valueOf(rs.getString("status")));
        return tx;
    }
}
