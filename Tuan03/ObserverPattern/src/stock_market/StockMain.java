package stock_market;

public class StockMain {
    public static void main(String[] args) {
        Stock vnm   = new Stock("VNM",  85_000);
        Stock vnpay = new Stock("VPB",  20_500);

        Investor alice = new Investor("Alice");
        Investor bob   = new Investor("Bob");
        StockBot bot1  = new StockBot("AlphaBot", 3.0);

        vnm.addObserver(alice);
        vnm.addObserver(bob);
        vnm.addObserver(bot1);
        vnpay.addObserver(alice);

        System.out.println("=== Phien giao dich ===");
        vnm.setPrice(87_500);   // +2.9%
        vnm.setPrice(81_000);   // -7.4% → bot kích hoạt
        vnpay.setPrice(21_200); // Alice theo dõi VPB

        // Bob hủy theo dõi VNM
        System.out.println("\nBob huy theo doi VNM");
        vnm.removeObserver(bob);
        vnm.setPrice(83_000);
    }
}
