package decorator;

import model.Book;
import java.time.LocalDate;

/**
 * =============================
 *  DECORATOR PATTERN
 * =============================
 * Interface gốc – hành động mượn sách cơ bản.
 */
public interface BorrowingService {
    /**
     * Thực hiện mượn sách.
     * @return LocalDate – ngày đến hạn trả
     */
    LocalDate borrow(Book book, String userId);

    /** Mô tả dịch vụ mượn đang áp dụng */
    String getDescription();
}
