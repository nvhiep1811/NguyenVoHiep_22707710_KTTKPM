package task_management;

public class EmailNotifier implements TaskObserver {
    private String emailDomain;

    public EmailNotifier(String domain) { this.emailDomain = domain; }

    @Override
    public void onTaskUpdated(TaskEvent e) {
        // Chỉ gửi email khi task hoàn thành hoặc bị block
        if (e.getNewStatus() == TaskStatus.DONE
                || e.getNewStatus() == TaskStatus.BLOCKED) {
            System.out.printf("  [Email] GUI toi team@%s: Task \"%s\" chuyen sang %s (by %s)\n",
                    emailDomain, e.getTaskTitle(), e.getNewStatus(), e.getChangedBy());
        }
    }
}
