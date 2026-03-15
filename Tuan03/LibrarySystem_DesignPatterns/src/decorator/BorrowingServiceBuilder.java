package decorator;

/**
 * Helper class để tạo BorrowingService với các decorator theo yêu cầu.
 * Client dùng class này thay vì tự lồng decorator thủ công.
 */
public class BorrowingServiceBuilder {

    private BorrowingService service;

    public BorrowingServiceBuilder() {
        this.service = new BasicBorrowingService();
    }

    public BorrowingServiceBuilder withExtension() {
        this.service = new ExtendedLoanDecorator(service);
        return this;
    }

    public BorrowingServiceBuilder withBraille() {
        this.service = new BrailleVersionDecorator(service);
        return this;
    }

    public BorrowingServiceBuilder withTranslation(String language) {
        this.service = new TranslatedVersionDecorator(service, language);
        return this;
    }

    public BorrowingService build() {
        return service;
    }
}
