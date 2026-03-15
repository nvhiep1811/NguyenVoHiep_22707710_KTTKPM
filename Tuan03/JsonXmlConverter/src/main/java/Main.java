public class Main {
    public static void main(String[] args) {

        // Wiring: inject Adapter vào WebService
        XmlSystem     xmlSystem = new XmlSystem();
        DataConverter adapter   = new XmlJsonAdapter(xmlSystem);
        WebService    service   = new WebService(adapter);

        // ── Case 1: XmlSystem gửi XML lên → WebService cần JSON ──
        String incomingXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <user>
                  <id>1001</id>
                  <name>Nguyen Van A</name>
                  <email>vana@example.com</email>
                  <role>admin</role>
                </user>
                """;

        System.out.println("══════════════════════════════════");
        System.out.println(" CASE 1: XML → JSON");
        System.out.println("══════════════════════════════════");
        service.handleXmlRequest(incomingXml);

        // ── Case 2: WebService xử lý xong, gửi JSON xuống XmlSystem ──
        String responseJson = """
                {
                  "user": {
                    "id": "1001",
                    "name": "Nguyen Van A",
                    "email": "vana@example.com",
                    "role": "admin",
                    "status": "updated"
                  }
                }
                """;

        System.out.println("\n══════════════════════════════════");
        System.out.println(" CASE 2: JSON → XML");
        System.out.println("══════════════════════════════════");
        service.sendJsonAsXml(responseJson);

        // ── Case 3: XML không hợp lệ ──
        System.out.println("\n══════════════════════════════════");
        System.out.println(" CASE 3: XML khong hop le");
        System.out.println("══════════════════════════════════");
        service.handleXmlRequest("<broken>xml<without>closing");
    }
}
