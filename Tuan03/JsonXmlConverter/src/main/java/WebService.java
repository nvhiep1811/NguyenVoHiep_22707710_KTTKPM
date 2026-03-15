public class WebService {
    private final DataConverter converter;

    public WebService(DataConverter converter) {
        this.converter = converter;
    }

    /** Nhận request dạng XML từ hệ thống cũ, xử lý như JSON */
    public void handleXmlRequest(String xmlData) {
        System.out.println("\n[WebService] Nhan request dang XML...");
        try {
            String json = converter.toJson(xmlData);
            System.out.println("[WebService] Da xu ly thanh JSON:\n" + json);
        } catch (Exception e) {
            System.out.println("[WebService] Loi: " + e.getMessage());
        }
    }

    /** Gửi response JSON sang hệ thống cũ dưới dạng XML */
    public void sendJsonAsXml(String jsonData) {
        System.out.println("\n[WebService] Gui response xuong XmlSystem...");
        try {
            String xml = converter.toXml(jsonData);
            System.out.println("[WebService] Da chuyen JSON → XML:\n" + xml);
        } catch (Exception e) {
            System.out.println("[WebService] Loi: " + e.getMessage());
        }
    }
}
