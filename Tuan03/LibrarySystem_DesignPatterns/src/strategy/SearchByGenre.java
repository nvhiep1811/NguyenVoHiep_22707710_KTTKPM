package strategy;

import model.Book;
import java.util.List;
import java.util.stream.Collectors;

/** Tìm kiếm theo thể loại */
public class SearchByGenre implements SearchStrategy {
    @Override
    public List<Book> search(List<Book> books, String keyword) {
        String kw = keyword.toLowerCase();
        return books.stream()
                .filter(b -> b.getGenre().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }
    @Override public String getStrategyName() { return "Theo Thể Loại"; }
}
