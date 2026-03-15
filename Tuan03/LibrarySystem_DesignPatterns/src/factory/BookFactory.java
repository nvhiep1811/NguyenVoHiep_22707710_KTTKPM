package factory;

import model.*;

/**
 * =============================
 *  FACTORY METHOD PATTERN
 * =============================
 * BookFactory chịu trách nhiệm tạo ra các loại sách khác nhau.
 * Client không cần biết class cụ thể nào được khởi tạo.
 */
public class BookFactory {

    public enum BookType { PAPER, EBOOK, AUDIO }

    /**
     * Factory method trung tâm – tạo sách dựa theo type.
     * Tham số extras:
     *   PAPER  -> extras[0] = số trang (int)
     *   EBOOK  -> extras[0] = format (String)
     *   AUDIO  -> extras[0] = durationMinutes (int), extras[1] = narrator (String)
     */
    public static Book createBook(BookType type,
                                  String id,
                                  String title,
                                  String author,
                                  String genre,
                                  Object... extras) {
        return switch (type) {
            case PAPER -> {
                int pages = (extras.length > 0) ? (int) extras[0] : 200;
                yield new PaperBook(id, title, author, genre, pages);
            }
            case EBOOK -> {
                String format = (extras.length > 0) ? (String) extras[0] : "PDF";
                yield new EBook(id, title, author, genre, format);
            }
            case AUDIO -> {
                int mins      = (extras.length > 0) ? (int)    extras[0] : 60;
                String narr   = (extras.length > 1) ? (String) extras[1] : "Không rõ";
                yield new AudioBook(id, title, author, genre, mins, narr);
            }
        };
    }
}
