package task_management;

public interface TaskSubject {
    void addObserver(TaskObserver o);
    void removeObserver(TaskObserver o);
    void notifyObservers(TaskEvent event);
}
