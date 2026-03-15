package stock_market;

import java.util.ArrayList;
import java.util.List;

public class Stock implements StockSubject {
    private String symbol;
    private double price;
    private double prevPrice;
    private List<StockObserver> observers = new ArrayList<>();

    public Stock(String symbol, double initialPrice) {
        this.symbol = symbol;
        this.price  = initialPrice;
        this.prevPrice = initialPrice;
    }

    @Override public void addObserver(StockObserver o)    { observers.add(o); }
    @Override public void removeObserver(StockObserver o) { observers.remove(o); }

    @Override
    public void notifyObservers() {
        for (StockObserver o : observers)
            o.onPriceChange(symbol, prevPrice, price);
    }

    // Khi giá thay đổi → tự động notify
    public void setPrice(double newPrice) {
        this.prevPrice = this.price;
        this.price     = newPrice;
        System.out.println("\n[Stock] " + symbol + ": $" + prevPrice + " → $" + price);
        notifyObservers();
    }

    public String getSymbol() { return symbol; }
    public double getPrice()  { return price;  }
}
