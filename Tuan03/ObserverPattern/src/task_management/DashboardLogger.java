package task_management;

import java.util.ArrayList;
import java.util.List;

public class DashboardLogger implements TaskObserver {
    private List<String> log = new ArrayList<>();

    @Override
    public void onTaskUpdated(TaskEvent e) {
        String entry = String.format("[%s] %s: %s → %s",
                e.getTimestamp(), e.getTaskTitle(),
                e.getOldStatus(), e.getNewStatus());
        log.add(entry);
        System.out.println("  [Dashboard] Ghi log: " + entry);
    }

    public void printLog() {
        System.out.println("\n=== Activity Log ===");
        log.forEach(l -> System.out.println("  " + l));
    }
}
