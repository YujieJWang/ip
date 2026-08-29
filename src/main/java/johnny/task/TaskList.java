package johnny.task;

import java.util.ArrayList;

/**
 * Manages an ordered list of tasks with operations to add, delete,
 * and retrieve tasks.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list pre-populated with the given tasks. */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void add(Task task) {
        tasks.add(task);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    /** Returns the underlying list for serialization by Storage. */
    public ArrayList<Task> getAll() {
        return tasks;
    }
}
