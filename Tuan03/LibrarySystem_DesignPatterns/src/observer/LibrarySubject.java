package observer;

import model.Book;

/**
 * =============================
 *  OBSERVER PATTERN
 * =============================
 */

// ---------- Interface Subject ----------
public interface LibrarySubject {
    void addObserver(LibraryObserver observer);
    void removeObserver(LibraryObserver observer);
    void notifyObservers(String eventType, Book book);
}
