package task_management;

public class TeamMember implements TaskObserver {
    private String name;

    public TeamMember(String name) { this.name = name; }

    @Override
    public void onTaskUpdated(TaskEvent e) {
        System.out.printf("  [%s] Nhan TB [%s]: \"%s\" → %s\n",
                name, e.getTimestamp(), e.getTaskTitle(), e.getNewStatus());
    }
}
