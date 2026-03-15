package model;

/** Sách điện tử */
public class EBook extends Book {
    private final String format; // PDF, EPUB, MOBI

    public EBook(String id, String title, String author, String genre, String format) {
        super(id, title, author, genre);
        this.format = format;
    }

    @Override public String getType()        { return "EBOOK"; }
    @Override public String getDescription() { return "Định dạng: " + format; }
}
