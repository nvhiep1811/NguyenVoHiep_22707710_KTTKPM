public class Main {
    public static void main(String[] args) {

        // --- Tạo các file lá ---
        File readme   = new File("README.md",     2048,  "Markdown");
        File main     = new File("Main.java",     5120,  "Java");
        File config   = new File("config.yaml",   1024,  "YAML");
        File logo     = new File("logo.png",      40960, "PNG");
        File test     = new File("AppTest.java",  3072,  "Java");

        // --- Tạo cấu trúc thư mục ---
        Directory src  = new Directory("src");
        src.add(main);

        Directory test_dir = new Directory("test");
        test_dir.add(test);

        Directory assets = new Directory("assets");
        assets.add(logo);

        Directory project = new Directory("my-project");
        project.add(readme);
        project.add(config);
        project.add(src);
        project.add(test_dir);
        project.add(assets);

        // --- Hiển thị toàn bộ cây ---
        System.out.println("=== Cấu trúc thư mục ===");
        project.display("");

        // --- Client xử lý đồng nhất: không cần biết File hay Directory ---
        System.out.println("\n=== Duyệt children của project ===");
        for (int i = 0; i < 5; i++) {
            FileSystemComponent c = project.getChild(i);
            System.out.print("  [" + c.getClass().getSimpleName() + "] ");
            c.display("");
        }
    }
}