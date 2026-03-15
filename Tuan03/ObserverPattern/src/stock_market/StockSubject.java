package stock_market;

public interface StockSubject {
    void addObserver(StockObserver o);
    void removeObserver(StockObserver o);
    void notifyObservers();
}
