package johnny.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import johnny.JohnnyException;
import johnny.task.Deadline;
import johnny.task.Event;
import johnny.task.Todo;

/**
 * Parses user input into commands and task objects.
 * All methods are static since Parser holds no state.
 */
public class Parser {

    /**
     * Extracts the command keyword from the user's input.
     * Returns Command.UNKNOWN for unrecognized keywords.
     */
    public static Command parseCommand(String input) {
        String keyword = input.split(" ", 2)[0].toUpperCase();
        try {
            return Command.valueOf(keyword);
        } catch (IllegalArgumentException e) {
            return Command.UNKNOWN;
        }
    }

    /**
     * Extracts the argument portion of the user's input (everything after
     * the first word). Returns an empty string if there are no arguments.
     */
    public static String parseArguments(String input) {
        String[] parts = input.split(" ", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    /**
     * Parses a 1-based task index from the user's argument string.
     * Throws JohnnyException if the argument is missing, non-numeric, or out of range.
     */
    public static int parseTaskIndex(String arguments, int taskCount) throws JohnnyException {
        if (arguments.trim().isEmpty()) {
            throw new JohnnyException("Please provide a task number.");
        }
        int index;
        try {
            index = Integer.parseInt(arguments.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new JohnnyException("'" + arguments.trim() + "' is not a valid task number.");
        }
        if (index < 0 || index >= taskCount) {
            throw new JohnnyException("Task number " + (index + 1) + " is out of range. "
                    + "You have " + taskCount + " tasks.");
        }
        return index;
    }

    /**
     * Parses the arguments of a "todo" command into a Todo task.
     */
    public static Todo parseTodo(String arguments) throws JohnnyException {
        if (arguments.trim().isEmpty()) {
            throw new JohnnyException("The description of a todo cannot be empty.");
        }
        return new Todo(arguments.trim());
    }

    /**
     * Parses the arguments of a "deadline" command into a Deadline task.
     * Expected format: {@code <description> /by <yyyy-MM-dd>}
     */
    public static Deadline parseDeadline(String arguments) throws JohnnyException {
        int byIndex = arguments.indexOf(" /by ");
        if (byIndex == -1) {
            throw new JohnnyException(
                    "Invalid deadline format. Use: deadline <description> /by <date>");
        }
        String description = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + 5).trim();
        if (description.isEmpty()) {
            throw new JohnnyException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new JohnnyException("The deadline date cannot be empty.");
        }
        LocalDate byDate;
        try {
            byDate = LocalDate.parse(by);
        } catch (DateTimeParseException e) {
            throw new JohnnyException(
                    "Invalid date format. Please use yyyy-MM-dd (e.g., 2019-10-15).");
        }
        return new Deadline(description, byDate);
    }

    /**
     * Parses the arguments of an "event" command into an Event task.
     * Expected format: {@code <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>}
     */
    public static Event parseEvent(String arguments) throws JohnnyException {
        int fromIndex = arguments.indexOf(" /from ");
        int toIndex = arguments.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1) {
            throw new JohnnyException(
                    "Invalid event format. Use: event <description> /from <date> /to <date>");
        }
        if (fromIndex > toIndex) {
            throw new JohnnyException(
                    "Invalid event format. /from must come before /to.");
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + 7, toIndex).trim();
        String to = arguments.substring(toIndex + 5).trim();
        if (description.isEmpty()) {
            throw new JohnnyException("The description of an event cannot be empty.");
        }
        if (from.isEmpty()) {
            throw new JohnnyException("The start date of an event cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new JohnnyException("The end date of an event cannot be empty.");
        }
        LocalDate fromDate;
        LocalDate toDate;
        try {
            fromDate = LocalDate.parse(from);
        } catch (DateTimeParseException e) {
            throw new JohnnyException(
                    "Invalid start date format. Please use yyyy-MM-dd (e.g., 2019-10-15).");
        }
        try {
            toDate = LocalDate.parse(to);
        } catch (DateTimeParseException e) {
            throw new JohnnyException(
                    "Invalid end date format. Please use yyyy-MM-dd (e.g., 2019-10-15).");
        }
        return new Event(description, fromDate, toDate);
    }
}
