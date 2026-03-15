package observer;

import model.Book;

/** Nhân viên thư viện – nhận mọi loại thông báo */
public class LibraryStaff implements LibraryObserver {
    private final String name;

    public LibraryStaff(String name) { this.name = name; }

    @Override
    public void update(String eventType, Book book) {
        String msg = switch (eventType) {
            case "NEW_BOOK"      -> "📚 Sách mới được thêm: \"" + book.getTitle() + "\" (" + book.getType() + ")";
            case "BOOK_BORROWED" -> "📤 Sách vừa được mượn: \"" + book.getTitle() + "\" - hạn: " + book.getDueDate();
            case "BOOK_RETURNED" -> "📥 Sách vừa được trả: \"" + book.getTitle() + "\"";
            case "BOOK_OVERDUE"  -> "⚠️  QUÁN HẠN: \"" + book.getTitle() + "\" (mượn bởi: " + book.getBorrowedBy() + ")";
            default              -> "🔔 Sự kiện: " + eventType + " - " + book.getTitle();
        };
        System.out.printf("  [Nhân viên %-10s] %s%n", name, msg);
    }

    @Override public String getName() { return name; }
}
