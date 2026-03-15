package stock_market;

public class StockBot implements StockObserver {
    private String name;
    private double threshold; // % biến động kích hoạt lệnh

    public StockBot(String name, double threshold) {
        this.name      = name;
        this.threshold = threshold;
    }

    @Override
    public void onPriceChange(String symbol, double oldPrice, double newPrice) {
        double change = Math.abs((newPrice - oldPrice) / oldPrice) * 100;
        if (change >= threshold) {
            String action = newPrice > oldPrice ? "MUA" : "BAN";
            System.out.printf("  [Bot] %s: bien dong %.1f%% >= %.1f%% → DAT LENH %s %s!\n",
                    name, change, threshold, action, symbol);
        } else {
            System.out.printf("  [Bot] %s: bien dong %.1f%% < %.1f%% → bo qua.\n",
                    name, change, threshold);
        }
    }
}
