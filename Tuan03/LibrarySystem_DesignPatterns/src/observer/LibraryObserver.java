package observer;

import model.Book;

/** Interface Observer – nhận thông báo từ thư viện */
public interface LibraryObserver {
    void update(String eventType, Book book);
    String getName();
}
