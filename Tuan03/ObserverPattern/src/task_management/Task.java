package task_management;

import java.util.ArrayList;
import java.util.List;

public class Task implements TaskSubject {
    private String     title;
    private TaskStatus status;
    private String     assignee;
    private List<TaskObserver> observers = new ArrayList<>();

    public Task(String title, String assignee) {
        this.title    = title;
        this.status   = TaskStatus.TODO;
        this.assignee = assignee;
    }

    @Override public void addObserver(TaskObserver o)    { observers.add(o); }
    @Override public void removeObserver(TaskObserver o) { observers.remove(o); }

    @Override
    public void notifyObservers(TaskEvent event) {
        for (TaskObserver o : observers)
            o.onTaskUpdated(event);
    }

    // Thay đổi trạng thái → tự động notify
    public void updateStatus(TaskStatus newStatus, String updatedBy) {
        if (this.status == newStatus) return;
        TaskStatus old = this.status;
        this.status    = newStatus;
        System.out.println("\n[Task] \"" + title + "\": "
                + old + " → " + newStatus + " (by " + updatedBy + ")");
        notifyObservers(new TaskEvent(title, old, newStatus, updatedBy));
    }

    public String     getTitle()    { return title;    }
    public TaskStatus getStatus()   { return status;   }
    public String     getAssignee() { return assignee; }
}
