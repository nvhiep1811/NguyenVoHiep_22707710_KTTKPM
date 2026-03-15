package task_management;

public class TaskMain {
    public static void main(String[] args) {
        Task loginTask = new Task("Implement Login Feature", "Alice");
        Task apiTask   = new Task("Build REST API",          "Bob");

        TeamMember    alice     = new TeamMember("Alice");
        TeamMember    bob       = new TeamMember("Bob");
        TeamMember    carol     = new TeamMember("Carol (PM)");
        EmailNotifier emailer   = new EmailNotifier("company.vn");
        DashboardLogger logger  = new DashboardLogger();

        // Đăng ký observers
        loginTask.addObserver(alice);
        loginTask.addObserver(bob);
        loginTask.addObserver(carol);
        loginTask.addObserver(emailer);
        loginTask.addObserver(logger);

        apiTask.addObserver(carol);
        apiTask.addObserver(emailer);
        apiTask.addObserver(logger);

        System.out.println("=== Sprint Progress ===");
        loginTask.updateStatus(TaskStatus.IN_PROGRESS, "Alice");
        loginTask.updateStatus(TaskStatus.BLOCKED,     "Alice");
        loginTask.updateStatus(TaskStatus.IN_PROGRESS, "DevOps");
        loginTask.updateStatus(TaskStatus.DONE,        "Alice");

        apiTask.updateStatus(TaskStatus.IN_PROGRESS, "Bob");
        apiTask.updateStatus(TaskStatus.DONE,        "Bob");

        logger.printLog();
    }
}
