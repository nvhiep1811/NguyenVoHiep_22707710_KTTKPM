package strategy;

import model.Book;
import java.util.List;
import java.util.stream.Collectors;

/** Tìm kiếm theo tác giả */
public class SearchByAuthor implements SearchStrategy {
    @Override
    public List<Book> search(List<Book> books, String keyword) {
        String kw = keyword.toLowerCase();
        return books.stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }
    @Override public String getStrategyName() { return "Theo Tác Giả"; }
}
