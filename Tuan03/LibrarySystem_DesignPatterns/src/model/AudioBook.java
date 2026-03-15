package model;

/** Sách nói */
public class AudioBook extends Book {
    private final int durationMinutes;
    private final String narrator;

    public AudioBook(String id, String title, String author,
                     String genre, int durationMinutes, String narrator) {
        super(id, title, author, genre);
        this.durationMinutes = durationMinutes;
        this.narrator        = narrator;
    }

    @Override public String getType()        { return "AUDIO"; }
    @Override public String getDescription() { return durationMinutes + " phút, đọc bởi " + narrator; }
}
