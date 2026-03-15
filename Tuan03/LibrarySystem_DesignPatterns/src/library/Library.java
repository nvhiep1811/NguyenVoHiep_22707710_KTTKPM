package library;

import factory.BookFactory;
import model.Book;
import observer.LibraryObserver;
import observer.LibrarySubject;
import strategy.SearchStrategy;
import strategy.SearchByTitle;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * =============================
 *  SINGLETON PATTERN
 * =============================
 * Library là đối tượng duy nhất quản lý toàn bộ hệ thống.
 * Cũng implement LibrarySubject (Observer Pattern).
 */
public class Library implements LibrarySubject {
    private final String name;
    private final Map<String, Book>        books     = new LinkedHashMap<>();
    private final List<LibraryObserver>    observers = new ArrayList<>();
    private SearchStrategy                 searchStrategy = new SearchByTitle();
    private int                            idCounter = 1;

    // ── Singleton: instance duy nhất ──────────────────────────
    private static Library instance;

    public static Library getInstance() {
        if (instance == null) {
            synchronized (Library.class) {
                if (instance == null) {
                    instance = new Library("Thư Viện Trung Tâm");
                }
            }
        }
        return instance;
    }

    // Constructor private – không cho phép tạo từ ngoài
    private Library(String name) {
        this.name = name;
        System.out.println("✅ Thư viện \"" + name + "\" đã được khởi tạo.");
    }

    // Observer
    @Override
    public void addObserver(LibraryObserver observer) {
        observers.add(observer);
        System.out.println("  👤 Đã đăng ký theo dõi: " + observer.getName());
    }

    @Override
    public void removeObserver(LibraryObserver observer) {
        observers.remove(observer);
        System.out.println("  ❌ Đã hủy theo dõi: " + observer.getName());
    }

    @Override
    public void notifyObservers(String eventType, Book book) {
        for (LibraryObserver obs : observers) {
            obs.update(eventType, book);
        }
    }

    // Factory
    public Book addBook(BookFactory.BookType type,
                        String title, String author, String genre,
                        Object... extras) {
        String id   = "B" + String.format("%03d", idCounter++);
        Book   book = BookFactory.createBook(type, id, title, author, genre, extras);
        books.put(id, book);
        System.out.println("  ➕ Sách mới [" + id + "]: " + title);
        notifyObservers("NEW_BOOK", book);
        return book;
    }

    // Strategy
    public void setSearchStrategy(SearchStrategy strategy) {
        this.searchStrategy = strategy;
        System.out.println("  🔍 Chiến lược tìm kiếm: " + strategy.getStrategyName());
    }

    public List<Book> search(String keyword) {
        return searchStrategy.search(new ArrayList<>(books.values()), keyword);
    }

    // MƯỢN / TRẢ SÁCH
    public boolean borrowBook(String bookId, String userId,
                               decorator.BorrowingService service) {
        Book book = books.get(bookId);
        if (book == null) {
            System.out.println("  ❌ Không tìm thấy sách: " + bookId);
            return false;
        }
        if (!book.isAvailable()) {
            System.out.println("  ❌ Sách \"" + book.getTitle() + "\" hiện không có sẵn.");
            return false;
        }
        LocalDate due = service.borrow(book, userId);
        System.out.printf("  ✅ %s đã mượn \"%s\" | Hạn trả: %s | Dịch vụ: %s%n",
                userId, book.getTitle(), due, service.getDescription());
        notifyObservers("BOOK_BORROWED", book);
        return true;
    }

    public boolean returnBook(String bookId) {
        Book book = books.get(bookId);
        if (book == null || book.isAvailable()) {
            System.out.println("  ❌ Không thể trả sách: " + bookId);
            return false;
        }
        System.out.printf("  ✅ Sách \"%s\" đã được trả bởi %s%n",
                book.getTitle(), book.getBorrowedBy());
        book.returnBook();
        notifyObservers("BOOK_RETURNED", book);
        return true;
    }

    // Kiểm tra và thông báo sách quá hạn
    public void checkOverdue() {
        LocalDate today = LocalDate.now();
        books.values().stream()
             .filter(b -> !b.isAvailable() && b.getDueDate() != null
                         && b.getDueDate().isBefore(today))
             .forEach(b -> notifyObservers("BOOK_OVERDUE", b));
    }

    // HIỂN THỊ
    public void listAllBooks() {
        System.out.println("\n  ┌─ Danh sách sách (" + books.size() + " cuốn) ─────────────────────────────────────────────────────────");
        books.values().forEach(b -> System.out.println("  │ " + b));
        System.out.println("  └───────────────────────────────────────────────────────────────────────────────────");
    }

    public List<Book> getAvailableBooks() {
        return books.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }

    public Book findById(String id) { return books.get(id); }
    public String getName()         { return name; }
}
