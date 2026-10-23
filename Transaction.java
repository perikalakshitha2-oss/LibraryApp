package com.library.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a borrow / return transaction.
 */
public class Transaction {

    public enum Status { BORROWED, RETURNED, OVERDUE }

    private int        transactionId;
    private int        memberId;
    private String     memberName;   // joined field for display
    private int        bookId;
    private String     bookTitle;    // joined field for display
    private LocalDate  borrowDate;
    private LocalDate  dueDate;
    private LocalDate  returnDate;
    private BigDecimal fineAmount;
    private Status     status;

    // ── Constructors ─────────────────────────────────────────

    public Transaction() {}

    public Transaction(int memberId, int bookId, LocalDate borrowDate, LocalDate dueDate) {
        this.memberId   = memberId;
        this.bookId     = bookId;
        this.borrowDate = borrowDate;
        this.dueDate    = dueDate;
        this.fineAmount = BigDecimal.ZERO;
        this.status     = Status.BORROWED;
    }

    // ── Getters & Setters ────────────────────────────────────

    public int        getTransactionId()              { return transactionId; }
    public void       setTransactionId(int id)        { this.transactionId = id; }

    public int        getMemberId()                   { return memberId; }
    public void       setMemberId(int id)             { this.memberId = id; }

    public String     getMemberName()                 { return memberName; }
    public void       setMemberName(String n)         { this.memberName = n; }

    public int        getBookId()                     { return bookId; }
    public void       setBookId(int id)               { this.bookId = id; }

    public String     getBookTitle()                  { return bookTitle; }
    public void       setBookTitle(String t)          { this.bookTitle = t; }

    public LocalDate  getBorrowDate()                 { return borrowDate; }
    public void       setBorrowDate(LocalDate d)      { this.borrowDate = d; }

    public LocalDate  getDueDate()                    { return dueDate; }
    public void       setDueDate(LocalDate d)         { this.dueDate = d; }

    public LocalDate  getReturnDate()                 { return returnDate; }
    public void       setReturnDate(LocalDate d)      { this.returnDate = d; }

    public BigDecimal getFineAmount()                 { return fineAmount; }
    public void       setFineAmount(BigDecimal f)     { this.fineAmount = f; }

    public Status     getStatus()                     { return status; }
    public void       setStatus(Status s)             { this.status = s; }

    @Override
    public String toString() {
        return String.format(
            "[%d] Member: %-20s | Book: %-35s | Borrowed: %s | Due: %s | Status: %-8s | Fine: ₹%.2f",
            transactionId, memberName, bookTitle, borrowDate, dueDate, status, fineAmount);
    }
}
