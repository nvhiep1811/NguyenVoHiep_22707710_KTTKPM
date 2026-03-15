package decorator;

import model.Book;
import java.time.LocalDate;

/** Dịch vụ mượn sách CƠ BẢN – mặc định 14 ngày */
public class BasicBorrowingService implements BorrowingService {
    private static final int DEFAULT_DAYS = 14;

    @Override
    public LocalDate borrow(Book book, String userId) {
        LocalDate due = LocalDate.now().plusDays(DEFAULT_DAYS);
        book.borrow(userId, due);
        return due;
    }

    @Override
    public String getDescription() {
        return "Mượn cơ bản (14 ngày)";
    }
}
