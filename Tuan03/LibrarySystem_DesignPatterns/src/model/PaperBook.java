package model;

/** Sách giấy truyền thống */
public class PaperBook extends Book {
    private final int pages;

    public PaperBook(String id, String title, String author, String genre, int pages) {
        super(id, title, author, genre);
        this.pages = pages;
    }

    @Override public String getType()        { return "PAPER"; }
    @Override public String getDescription() { return pages + " trang"; }
}
