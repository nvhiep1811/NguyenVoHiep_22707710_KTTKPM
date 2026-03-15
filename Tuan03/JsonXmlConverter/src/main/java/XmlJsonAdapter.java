import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class XmlJsonAdapter implements DataConverter {
    private final XmlSystem    xmlSystem;
    private final ObjectMapper objectMapper;

    public XmlJsonAdapter(XmlSystem xmlSystem) {
        this.xmlSystem    = xmlSystem;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * XML → JSON
     * Nhận XML từ XmlSystem, chuyển thành JSON cho WebService
     */
    @Override
    public String toJson(String xmlInput) throws Exception {
        System.out.println("  [Adapter] Chuyen XML → JSON...");

        // B1: Validate
        if (!xmlSystem.validateXml(xmlInput)) {
            throw new IllegalArgumentException("XML dau vao khong hop le!");
        }

        // B2: Dùng XmlSystem để parse XML → Map
        Map<String, String> data = xmlSystem.parseXml(xmlInput);

        // B3: Chuyển Map → JSON bằng Jackson
        String json = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(data);

        System.out.println("  [Adapter] Thanh cong: XML → JSON");
        return json;
    }

    /**
     * JSON → XML
     * Nhận JSON từ WebService, chuyển thành XML cho XmlSystem
     */
    @Override
    @SuppressWarnings("unchecked")
    public String toXml(String jsonInput) throws Exception {
        System.out.println("  [Adapter] Chuyen JSON → XML...");

        // B1: Jackson parse JSON → Map
        Map<String, Object> data = objectMapper.readValue(jsonInput, Map.class);

        // B2: Lấy root tag (key đầu tiên nếu có nested, hoặc dùng "root")
        String rootTag = "root";
        if (data.size() == 1) {
            Object val = data.values().iterator().next();
            if (val instanceof Map) {
                rootTag = data.keySet().iterator().next();
                data    = (Map<String, Object>) val;
            }
        }

        // B3: Dùng XmlSystem để build XML
        String xml = xmlSystem.buildXml(rootTag, data);

        System.out.println("  [Adapter] Thanh cong: JSON → XML");
        return xml;
    }
}
