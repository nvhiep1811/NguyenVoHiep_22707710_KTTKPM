package strategy;

import model.Book;
import java.util.List;

/**
 * =============================
 *  STRATEGY PATTERN
 * =============================
 * Interface chung cho tất cả chiến lược tìm kiếm.
 */
public interface SearchStrategy {
    List<Book> search(List<Book> books, String keyword);
    String getStrategyName();
}
