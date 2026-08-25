/**
 * Represents a task with a description, completion status, and optional type-specific fields.
 * Type can be "T" (todo), "D" (deadline), or "E" (event).
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected String type;
    protected String by;
    protected String from;
    protected String to;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.type = "T";
    }

    public Task(String description, String by) {
        this.description = description;
        this.isDone = false;
        this.type = "D";
        this.by = by;
    }

    public Task(String description, String from, String to) {
        this.description = description;
        this.isDone = false;
        this.type = "E";
        this.from = from;
        this.to = to;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsNotDone() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        String result = "[" + type + "][" + getStatusIcon() + "] " + description;
        if (type.equals("D")) {
            result += " (by: " + by + ")";
        } else if (type.equals("E")) {
            result += " (from: " + from + " to: " + to + ")";
        }
        return result;
    }
}
