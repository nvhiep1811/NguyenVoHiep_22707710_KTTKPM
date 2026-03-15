import decorator.BorrowingServiceBuilder;
import factory.BookFactory;
import library.Library;
import model.Book;
import observer.LibraryStaff;
import observer.RegisteredUser;
import strategy.SearchByAuthor;
import strategy.SearchByGenre;
import strategy.SearchByTitle;

import java.util.List;

/**
 * ================================================================
 *  DEMO – Hệ thống Quản lý Thư Viện
 *  Minh họa toàn bộ 5 Design Patterns:
 *    1. Singleton  → Library
 *    2. Factory    → BookFactory
 *    3. Strategy   → SearchBy*
 *    4. Observer   → LibraryStaff, RegisteredUser
 *    5. Decorator  → BorrowingServiceBuilder
 * ================================================================
 */
public class Main {

    static void section(String title) {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║  " + title);
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    public static void main(String[] args) {

        // ──────────────────────────────────────────────────────
        // 1. SINGLETON – lấy instance duy nhất của thư viện
        // ──────────────────────────────────────────────────────
        section("1. SINGLETON PATTERN");
        Library lib1 = Library.getInstance();
        Library lib2 = Library.getInstance();
        System.out.println("  lib1 == lib2 ? " + (lib1 == lib2));   // true
        Library lib = lib1;

        // ──────────────────────────────────────────────────────
        // 2. OBSERVER – đăng ký theo dõi thư viện
        // ──────────────────────────────────────────────────────
        section("2. OBSERVER PATTERN – Đăng ký theo dõi");
        lib.addObserver(new LibraryStaff("An"));
        lib.addObserver(new LibraryStaff("Bình"));
        lib.addObserver(new RegisteredUser("alice",   "Lập Trình"));
        lib.addObserver(new RegisteredUser("bob",     "Khoa Học"));
        lib.addObserver(new RegisteredUser("charlie", "Tiểu Thuyết"));

        // ──────────────────────────────────────────────────────
        // 3. FACTORY – thêm sách (Observer sẽ bắn NEW_BOOK)
        // ──────────────────────────────────────────────────────
        section("3. FACTORY METHOD – Thêm sách mới");

        Book b1 = lib.addBook(BookFactory.BookType.PAPER,
                "Clean Code",          "Robert C. Martin", "Lập Trình",   200);
        Book b2 = lib.addBook(BookFactory.BookType.PAPER,
                "Design Patterns",     "Gang of Four",     "Lập Trình",   395);
        Book b3 = lib.addBook(BookFactory.BookType.EBOOK,
                "A Brief History of Time","Stephen Hawking","Khoa Học",   "EPUB");
        Book b4 = lib.addBook(BookFactory.BookType.AUDIO,
                "Sapiens",             "Yuval Noah Harari","Lịch Sử",    780, "John Lee");
        Book b5 = lib.addBook(BookFactory.BookType.PAPER,
                "Dune",                "Frank Herbert",    "Tiểu Thuyết", 412);
        Book b6 = lib.addBook(BookFactory.BookType.EBOOK,
                "The Pragmatic Programmer","Hunt & Thomas","Lập Trình",  "PDF");

        // ──────────────────────────────────────────────────────
        // 4. STRATEGY – tìm kiếm với các chiến lược khác nhau
        // ──────────────────────────────────────────────────────
        section("4. STRATEGY PATTERN – Tìm kiếm sách");

        // 4a. Tìm theo tên
        lib.setSearchStrategy(new SearchByTitle());
        List<Book> r1 = lib.search("code");
        System.out.println("  Kết quả tìm \"code\":");
        r1.forEach(b -> System.out.println("    - " + b.getTitle() + " [" + b.getType() + "]"));

        // 4b. Tìm theo tác giả
        lib.setSearchStrategy(new SearchByAuthor());
        List<Book> r2 = lib.search("martin");
        System.out.println("  Kết quả tìm tác giả \"martin\":");
        r2.forEach(b -> System.out.println("    - " + b.getTitle() + " / " + b.getAuthor()));

        // 4c. Tìm theo thể loại
        lib.setSearchStrategy(new SearchByGenre());
        List<Book> r3 = lib.search("Lập Trình");
        System.out.println("  Kết quả tìm thể loại \"Lập Trình\":");
        r3.forEach(b -> System.out.println("    - " + b.getTitle()));

        // ──────────────────────────────────────────────────────
        // 5. DECORATOR – mượn sách với tính năng bổ sung
        // ──────────────────────────────────────────────────────
        section("5. DECORATOR PATTERN – Mượn sách");

        // 5a. Mượn cơ bản
        System.out.println("\n  [Mượn cơ bản]");
        var basic = new BorrowingServiceBuilder().build();
        lib.borrowBook(b1.getId(), "alice", basic);

        // 5b. Mượn + gia hạn 7 ngày
        System.out.println("\n  [Mượn + gia hạn]");
        var extended = new BorrowingServiceBuilder()
                .withExtension()
                .build();
        lib.borrowBook(b2.getId(), "bob", extended);

        // 5c. Mượn + bản dịch tiếng Anh
        System.out.println("\n  [Mượn + bản dịch]");
        var translated = new BorrowingServiceBuilder()
                .withTranslation("Tiếng Anh")
                .build();
        lib.borrowBook(b3.getId(), "charlie", translated);

        // 5d. Mượn + gia hạn + chữ nổi + dịch (stack decorator)
        System.out.println("\n  [Mượn + gia hạn + Braille + bản dịch]");
        var full = new BorrowingServiceBuilder()
                .withExtension()
                .withBraille()
                .withTranslation("Tiếng Việt")
                .build();
        lib.borrowBook(b5.getId(), "alice", full);

        // ──────────────────────────────────────────────────────
        // 6. Xem danh sách sách có sẵn
        // ──────────────────────────────────────────────────────
        section("6. DANH SÁCH SÁCH HIỆN TẠI");
        lib.listAllBooks();

        // ──────────────────────────────────────────────────────
        // 7. Trả sách → Observer nhận BOOK_RETURNED
        // ──────────────────────────────────────────────────────
        section("7. TRẢ SÁCH – Observer nhận thông báo");
        lib.returnBook(b1.getId());
        lib.returnBook(b2.getId());

        // ──────────────────────────────────────────────────────
        // 8. Kiểm tra quá hạn (demo với sách b3 – đặt ngày giả)
        // ──────────────────────────────────────────────────────
        section("8. KIỂM TRA QUÁ HẠN");
        // Giả lập sách b3 quá hạn bằng cách set thủ công
        b3.borrow("charlie", java.time.LocalDate.now().minusDays(3));
        System.out.println("  Chạy kiểm tra quá hạn...");
        lib.checkOverdue();

        // ──────────────────────────────────────────────────────
        System.out.println("\n✅ Demo hoàn thành!\n");
    }
}
