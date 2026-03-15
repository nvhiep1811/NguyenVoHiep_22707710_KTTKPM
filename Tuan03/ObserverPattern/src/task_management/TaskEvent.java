package task_management;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskEvent {
    private final String     taskTitle;
    private final TaskStatus oldStatus;
    private final TaskStatus newStatus;
    private final String     changedBy;
    private final String     timestamp;

    public TaskEvent(String title, TaskStatus oldStatus,
                     TaskStatus newStatus, String changedBy) {
        this.taskTitle  = title;
        this.oldStatus  = oldStatus;
        this.newStatus  = newStatus;
        this.changedBy  = changedBy;
        this.timestamp  = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String     getTaskTitle() { return taskTitle; }
    public TaskStatus getOldStatus() { return oldStatus; }
    public TaskStatus getNewStatus() { return newStatus; }
    public String     getChangedBy() { return changedBy; }
    public String     getTimestamp() { return timestamp; }
}
