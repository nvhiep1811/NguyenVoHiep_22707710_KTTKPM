package stock_market;

public interface StockObserver {
    void onPriceChange(String symbol, double oldPrice, double newPrice);
}
