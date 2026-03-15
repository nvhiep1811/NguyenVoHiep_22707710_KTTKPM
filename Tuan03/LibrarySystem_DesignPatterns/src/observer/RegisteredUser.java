package observer;

import model.Book;

/** Người dùng đăng ký theo dõi – chỉ quan tâm sách mới & quá hạn */
public class RegisteredUser implements LibraryObserver {
    private final String username;
    private final String favoriteGenre;

    public RegisteredUser(String username, String favoriteGenre) {
        this.username      = username;
        this.favoriteGenre = favoriteGenre;
    }

    @Override
    public void update(String eventType, Book book) {
        if ("NEW_BOOK".equals(eventType)) {
            // Chỉ thông báo nếu đúng thể loại yêu thích
            if (book.getGenre().equalsIgnoreCase(favoriteGenre)) {
                System.out.printf("  [Người dùng %-10s] 🎉 Sách mới đúng sở thích: \"%s\" (%s)%n",
                        username, book.getTitle(), book.getGenre());
            }
        } else if ("BOOK_OVERDUE".equals(eventType)
                   && username.equals(book.getBorrowedBy())) {
            System.out.printf("  [Người dùng %-10s] ⚠️  Nhắc nhở: Bạn đang quá hạn trả \"%s\"%n",
                    username, book.getTitle());
        }
    }

    @Override public String getName() { return username; }
}
