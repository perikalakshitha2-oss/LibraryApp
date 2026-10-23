package com.library.model;

/**
 * Represents a book in the library catalogue.
 */
public class Book {

    private int    bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int    publicationYear;
    private String genre;
    private int    totalCopies;
    private int    availableCopies;

    // ── Constructors ─────────────────────────────────────────

    public Book() {}

    public Book(String isbn, String title, String author,
                String publisher, int publicationYear,
                String genre, int totalCopies) {
        this.isbn            = isbn;
        this.title           = title;
        this.author          = author;
        this.publisher       = publisher;
        this.publicationYear = publicationYear;
        this.genre           = genre;
        this.totalCopies     = totalCopies;
        this.availableCopies = totalCopies;
    }

    // ── Getters & Setters ────────────────────────────────────

    public int    getBookId()           { return bookId; }
    public void   setBookId(int id)     { this.bookId = id; }

    public String getIsbn()             { return isbn; }
    public void   setIsbn(String isbn)  { this.isbn = isbn; }

    public String getTitle()            { return title; }
    public void   setTitle(String t)    { this.title = t; }

    public String getAuthor()           { return author; }
    public void   setAuthor(String a)   { this.author = a; }

    public String getPublisher()        { return publisher; }
    public void   setPublisher(String p){ this.publisher = p; }

    public int    getPublicationYear()          { return publicationYear; }
    public void   setPublicationYear(int year)  { this.publicationYear = year; }

    public String getGenre()            { return genre; }
    public void   setGenre(String g)    { this.genre = g; }

    public int    getTotalCopies()           { return totalCopies; }
    public void   setTotalCopies(int n)      { this.totalCopies = n; }

    public int    getAvailableCopies()       { return availableCopies; }
    public void   setAvailableCopies(int n)  { this.availableCopies = n; }

    @Override
    public String toString() {
        return String.format(
            "[%d] %-40s | %-25s | ISBN: %-18s | Available: %d/%d",
            bookId, title, author, isbn, availableCopies, totalCopies);
    }
}
