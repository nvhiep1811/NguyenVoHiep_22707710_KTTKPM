import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class XmlSystem {

    /** Parse XML string → Map<String, String> (key-value phẳng) */
    public Map<String, String> parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
        doc.getDocumentElement().normalize();

        Map<String, String> result = new LinkedHashMap<>();
        NodeList nodes = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                result.put(node.getNodeName(), node.getTextContent().trim());
            }
        }
        System.out.println("  [XmlSystem] Parsed XML → " + result.size() + " field(s)");
        return result;
    }

    /** Map<String, String> → build XML string */
    public String buildXml(String rootTag, Map<String, Object> data) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement(rootTag);
        doc.appendChild(root);

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Element el = doc.createElement(entry.getKey());
            el.appendChild(doc.createTextNode(String.valueOf(entry.getValue())));
            root.appendChild(el);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(sw));

        System.out.println("  [XmlSystem] Built XML with root <" + rootTag + ">");
        return sw.toString();
    }

    /** Kiểm tra XML hợp lệ không */
    public boolean validateXml(String xml) {
        try {
            DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
