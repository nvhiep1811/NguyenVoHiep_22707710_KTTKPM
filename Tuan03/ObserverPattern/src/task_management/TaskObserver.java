package task_management;

public interface TaskObserver {
    void onTaskUpdated(TaskEvent event);
}
