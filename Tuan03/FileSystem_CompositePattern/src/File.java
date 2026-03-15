public class File implements FileSystemComponent {
    private String name;
    private long size;   // bytes
    private String type;

    public File(String name, long size, String type) {
        this.name = name;
        this.size = size;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "📄 " + name
                + "  [" + type + ", " + size + " bytes]");
    }
}
