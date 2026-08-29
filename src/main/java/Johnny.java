import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Johnny {

    /**
     * Parses a 1-based task index from the user's argument string.
     * Throws JohnnyException if the argument is missing, non-numeric, or out of range.
     */
    private static int parseTaskIndex(String arguments, int taskCount) throws JohnnyException {
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

    private static void saveTasks(Storage storage, ArrayList<Task> tasks, Ui ui) {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            ui.showSaveError();
        }
    }

    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("./data/johnny.txt");
        ArrayList<Task> tasks;
        try {
            tasks = storage.load();
        } catch (Exception e) {
            ui.showLoadingError();
            tasks = new ArrayList<>();
        }

        ui.showGreeting();

        while (ui.hasNextLine()) {
            String input = ui.readCommand();
            ui.showLine();
            try {
                String[] parts = input.split(" ", 2);
                String arguments = parts.length > 1 ? parts[1] : "";
                Command command;
                try {
                    command = Command.valueOf(parts[0].toUpperCase());
                } catch (IllegalArgumentException e) {
                    command = Command.UNKNOWN;
                }

                switch (command) {
                case BYE:
                    ui.showFarewell();
                    break;
                case LIST:
                    ui.showTaskList(tasks);
                    break;
                case MARK:
                    int markIndex = parseTaskIndex(arguments, tasks.size());
                    tasks.get(markIndex).markAsDone();
                    ui.showTaskMarked(tasks.get(markIndex));
                    saveTasks(storage, tasks, ui);
                    break;
                case UNMARK:
                    int unmarkIndex = parseTaskIndex(arguments, tasks.size());
                    tasks.get(unmarkIndex).markAsNotDone();
                    ui.showTaskUnmarked(tasks.get(unmarkIndex));
                    saveTasks(storage, tasks, ui);
                    break;
                case DELETE:
                    int deleteIndex = parseTaskIndex(arguments, tasks.size());
                    Task removed = tasks.remove(deleteIndex);
                    ui.showTaskDeleted(removed, tasks.size());
                    saveTasks(storage, tasks, ui);
                    break;
                case TODO:
                    if (arguments.trim().isEmpty()) {
                        throw new JohnnyException("The description of a todo cannot be empty.");
                    }
                    tasks.add(new Todo(arguments.trim()));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(storage, tasks, ui);
                    break;
                case DEADLINE: {
                    int byIndex = arguments.indexOf(" /by ");
                    if (byIndex == -1) {
                        throw new JohnnyException(
                                "Invalid deadline format. Use: deadline <description> /by <date>");
                    }
                    String deadlineDesc = arguments.substring(0, byIndex).trim();
                    String by = arguments.substring(byIndex + 5).trim();
                    if (deadlineDesc.isEmpty()) {
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
                    tasks.add(new Deadline(deadlineDesc, byDate));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(storage, tasks, ui);
                    break;
                }
                case EVENT: {
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
                    String eventDesc = arguments.substring(0, fromIndex).trim();
                    String from = arguments.substring(fromIndex + 7, toIndex).trim();
                    String to = arguments.substring(toIndex + 5).trim();
                    if (eventDesc.isEmpty()) {
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
                    tasks.add(new Event(eventDesc, fromDate, toDate));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(storage, tasks, ui);
                    break;
                }
                case UNKNOWN:
                    throw new JohnnyException("I'm sorry, but I'm not too sure what that means :(");
                }
            } catch (JohnnyException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
            if (input.split(" ", 2)[0].equalsIgnoreCase("bye")) {
                break;
            }
        }

        ui.close();
    }
}
