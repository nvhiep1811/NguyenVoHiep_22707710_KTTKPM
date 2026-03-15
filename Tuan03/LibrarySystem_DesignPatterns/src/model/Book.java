package model;

import java.time.LocalDate;

/**
 * Abstract base class cho tất cả các loại sách.
 * Được dùng bởi Factory Method Pattern.
 */
public abstract class Book {
    private final String id;
    private final String title;
    private final String author;
    private final String genre;
    private boolean available;
    private String borrowedBy;
    private LocalDate dueDate;

    public Book(String id, String title, String author, String genre) {
        this.id        = id;
        this.title     = title;
        this.author    = author;
        this.genre     = genre;
        this.available = true;
    }

    // --- abstract ---
    public abstract String getType();
    public abstract String getDescription();

    // --- getters ---
    public String getId()        { return id; }
    public String getTitle()     { return title; }
    public String getAuthor()    { return author; }
    public String getGenre()     { return genre; }
    public boolean isAvailable() { return available; }
    public String getBorrowedBy(){ return borrowedBy; }
    public LocalDate getDueDate(){ return dueDate; }

    // --- borrow / return ---
    public void borrow(String userId, LocalDate dueDate) {
        this.available  = false;
        this.borrowedBy = userId;
        this.dueDate    = dueDate;
    }

    public void returnBook() {
        this.available  = true;
        this.borrowedBy = null;
        this.dueDate    = null;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-35s | %-20s | %-15s | %s | %s",
                getType(),
                title,
                author,
                genre,
                available ? "Có sẵn   " : "Đang mượn",
                available ? "" : "(hạn: " + dueDate + ")");
    }
}
