package johnny.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that needs to be done before a specific date.
 * The date is stored as a LocalDate and displayed as MMM dd yyyy.
 */
public class Deadline extends Task {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    protected LocalDate by;

    /**
     * Creates a deadline task with the given description and due date.
     *
     * @param description the task description
     * @param by the due date
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /** Returns file format: {@code D | done | description | yyyy-MM-dd}. */
    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
