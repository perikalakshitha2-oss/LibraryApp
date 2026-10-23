package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.MemberDAO;
import com.library.dao.TransactionDAO;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Business-logic facade used by the UI layer.
 * Keeps DAO calls and rules in one place.
 */
public class LibraryService {

    /** Fine per day for overdue books (in ₹). */
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("5.00");

    /** Default loan period in days. */
    private static final int LOAN_DAYS = 14;

    private final BookDAO        bookDAO        = new BookDAO();
    private final MemberDAO      memberDAO      = new MemberDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    // ════════════════════════════════════════════════════════
    //  BOOK operations
    // ════════════════════════════════════════════════════════

    public boolean addBook(Book book) throws SQLException {
        return bookDAO.addBook(book);
    }

    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.getAllBooks();
    }

    public List<Book> getAvailableBooks() throws SQLException {
        return bookDAO.getAvailableBooks();
    }

    public Book getBookById(int id) throws SQLException {
        return bookDAO.getBookById(id);
    }

    public List<Book> searchBooks(String keyword) throws SQLException {
        return bookDAO.searchBooks(keyword);
    }

    public boolean updateBook(Book book) throws SQLException {
        return bookDAO.updateBook(book);
    }

    public boolean deleteBook(int bookId) throws SQLException {
        // Refuse delete if copies are currently borrowed
        Book b = bookDAO.getBookById(bookId);
        if (b == null) return false;
        if (b.getAvailableCopies() < b.getTotalCopies()) {
            throw new IllegalStateException("Cannot delete book with copies currently borrowed.");
        }
        return bookDAO.deleteBook(bookId);
    }

    // ════════════════════════════════════════════════════════
    //  MEMBER operations
    // ════════════════════════════════════════════════════════

    public boolean addMember(Member member) throws SQLException {
        return memberDAO.addMember(member);
    }

    public List<Member> getAllMembers() throws SQLException {
        return memberDAO.getAllMembers();
    }

    public Member getMemberById(int id) throws SQLException {
        return memberDAO.getMemberById(id);
    }

    public List<Member> searchMembers(String keyword) throws SQLException {
        return memberDAO.searchMembers(keyword);
    }

    public boolean updateMember(Member member) throws SQLException {
        return memberDAO.updateMember(member);
    }

    public boolean deleteMember(int memberId) throws SQLException {
        return memberDAO.deleteMember(memberId);
    }

    // ════════════════════════════════════════════════════════
    //  TRANSACTION (Borrow / Return)
    // ════════════════════════════════════════════════════════

    /**
     * Borrow a book for a member.
     * @return the created Transaction, or throws on failure.
     */
    public Transaction borrowBook(int memberId, int bookId) throws SQLException {
        Member member = memberDAO.getMemberById(memberId);
        if (member == null)  throw new IllegalArgumentException("Member ID " + memberId + " not found.");
        if (!member.isActive()) throw new IllegalStateException("Member account is inactive.");

        Book book = bookDAO.getBookById(bookId);
        if (book == null)    throw new IllegalArgumentException("Book ID " + bookId + " not found.");
        if (book.getAvailableCopies() <= 0)
            throw new IllegalStateException("No available copies of \"" + book.getTitle() + "\".");

        LocalDate today  = LocalDate.now();
        LocalDate dueDate = today.plusDays(LOAN_DAYS);

        Transaction tx = new Transaction(memberId, bookId, today, dueDate);
        transactionDAO.addTransaction(tx);
        bookDAO.decrementAvailableCopies(bookId);

        tx.setMemberName(member.getName());
        tx.setBookTitle (book.getTitle());
        return tx;
    }

    /**
     * Return a borrowed book.
     * Calculates fine automatically for overdue returns.
     */
    public Transaction returnBook(int transactionId) throws SQLException {
        Transaction tx = transactionDAO.getTransactionById(transactionId);
        if (tx == null) throw new IllegalArgumentException("Transaction ID " + transactionId + " not found.");
        if (tx.getStatus() == Transaction.Status.RETURNED)
            throw new IllegalStateException("This book has already been returned.");

        LocalDate today = LocalDate.now();
        tx.setReturnDate(today);

        // Calculate fine
        if (today.isAfter(tx.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(tx.getDueDate(), today);
            tx.setFineAmount(FINE_PER_DAY.multiply(new BigDecimal(overdueDays)));
        } else {
            tx.setFineAmount(BigDecimal.ZERO);
        }
        tx.setStatus(Transaction.Status.RETURNED);

        transactionDAO.returnBook(tx);
        bookDAO.incrementAvailableCopies(tx.getBookId());
        return tx;
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        return transactionDAO.getAllTransactions();
    }

    public List<Transaction> getActiveTransactions() throws SQLException {
        // First refresh overdue statuses
        transactionDAO.markOverdueTransactions();
        return transactionDAO.getActiveTransactions();
    }

    public List<Transaction> getTransactionsByMember(int memberId) throws SQLException {
        return transactionDAO.getTransactionsByMember(memberId);
    }
}
