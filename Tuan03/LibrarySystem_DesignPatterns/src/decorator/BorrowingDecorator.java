package decorator;

import model.Book;
import java.time.LocalDate;

/**
 * Abstract Decorator – wrap BorrowingService khác.
 * Các concrete decorator kế thừa lớp này.
 */
public abstract class BorrowingDecorator implements BorrowingService {
    protected final BorrowingService wrapped;

    public BorrowingDecorator(BorrowingService wrapped) {
        this.wrapped = wrapped;
    }
}

// ─────────────────────────────────────────────────────────────
// Concrete Decorator 1: Gia hạn thêm 7 ngày
// ─────────────────────────────────────────────────────────────
class ExtendedLoanDecorator extends BorrowingDecorator {
    private static final int EXTRA_DAYS = 7;

    public ExtendedLoanDecorator(BorrowingService wrapped) {
        super(wrapped);
    }

    @Override
    public LocalDate borrow(Book book, String userId) {
        LocalDate due = wrapped.borrow(book, userId).plusDays(EXTRA_DAYS);
        book.borrow(userId, due); // cập nhật lại ngày mới
        return due;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Gia hạn 7 ngày";
    }
}

// ─────────────────────────────────────────────────────────────
// Concrete Decorator 2: Phiên bản chữ nổi (Braille)
// ─────────────────────────────────────────────────────────────
class BrailleVersionDecorator extends BorrowingDecorator {
    public BrailleVersionDecorator(BorrowingService wrapped) {
        super(wrapped);
    }

    @Override
    public LocalDate borrow(Book book, String userId) {
        System.out.println("  📖 Chuẩn bị phiên bản chữ nổi (Braille) cho: " + book.getTitle());
        return wrapped.borrow(book, userId);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Phiên bản Braille";
    }
}

// ─────────────────────────────────────────────────────────────
// Concrete Decorator 3: Bản dịch tiếng Anh
// ─────────────────────────────────────────────────────────────
class TranslatedVersionDecorator extends BorrowingDecorator {
    private final String targetLanguage;

    public TranslatedVersionDecorator(BorrowingService wrapped, String targetLanguage) {
        super(wrapped);
        this.targetLanguage = targetLanguage;
    }

    @Override
    public LocalDate borrow(Book book, String userId) {
        System.out.println("  🌐 Đính kèm bản dịch (" + targetLanguage + ") cho: " + book.getTitle());
        return wrapped.borrow(book, userId);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Bản dịch " + targetLanguage;
    }
}
