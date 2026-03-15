public interface DataConverter {
    String toJson(String xmlInput)  throws Exception;
    String toXml(String jsonInput)  throws Exception;
}
