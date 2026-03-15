package strategy;

import model.Book;
import java.util.List;
import java.util.stream.Collectors;

/** Tìm kiếm theo tên sách (không phân biệt hoa/thường) */
public class SearchByTitle implements SearchStrategy {
    @Override
    public List<Book> search(List<Book> books, String keyword) {
        String kw = keyword.toLowerCase();
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }
    @Override public String getStrategyName() { return "Theo Tên Sách"; }
}
