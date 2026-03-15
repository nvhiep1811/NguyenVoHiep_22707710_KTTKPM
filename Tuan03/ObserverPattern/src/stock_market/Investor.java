package stock_market;

public class Investor implements StockObserver {
    private String name;

    public Investor(String name) { this.name = name; }

    @Override
    public void onPriceChange(String symbol, double oldPrice, double newPrice) {
        double change = ((newPrice - oldPrice) / oldPrice) * 100;
        String trend  = change >= 0 ? "+" : "";
        System.out.printf("  [Investor] %s nhan TB: %s %s%.2f%% → hanh dong!\n",
                name, symbol, trend, change);
    }
}
