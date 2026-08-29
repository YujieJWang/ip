import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Johnny {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "     _       _                       \n"
            + "    | | ___ | |__  _ __  _ __  _   _ \n"
            + " _  | |/ _ \\| '_ \\| '_ \\| '_ \\| | | |\n"
            + "| |_| | (_) | | | | | | | | | | |_| |\n"
            + " \\___/ \\___/|_| |_|_| |_|_| |_|\\__, |\n"
            + "                                |___/ \n";

    private static void printLine() {
        System.out.println("    " + LINE);
    }

    private static void printIndented(String message) {
        System.out.println("     " + message);
    }

    private static void printTaskAdded(Task task, int taskCount) {
        printIndented("Got it. I've added this task:");
        printIndented("  " + task);
        printIndented("Now you have " + taskCount + " tasks in the list.");
    }

    private static void saveTasks(Storage storage, ArrayList<Task> tasks) {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            printIndented("Warning: Could not save tasks to disk.");
        }
    }

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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage("./data/johnny.txt");
        ArrayList<Task> tasks;
        try {
            tasks = storage.load();
        } catch (Exception e) {
            printIndented("Warning: Could not load saved tasks. Starting with an empty list.");
            tasks = new ArrayList<>();
        }

        printLine();
        System.out.print(BANNER);
        printIndented("Hello! I'm Johnny.");
        printIndented("What can I do for you?");
        printLine();

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            printLine();
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
                    printIndented("Bye bye! See you again soon.");
                    break;
                case LIST:
                    printIndented("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        printIndented((i + 1) + "." + tasks.get(i));
                    }
                    break;
                case MARK:
                    int markIndex = parseTaskIndex(arguments, tasks.size());
                    tasks.get(markIndex).markAsDone();
                    printIndented("Nice! I've marked this task as done:");
                    printIndented("  " + tasks.get(markIndex));
                    saveTasks(storage, tasks);
                    break;
                case UNMARK:
                    int unmarkIndex = parseTaskIndex(arguments, tasks.size());
                    tasks.get(unmarkIndex).markAsNotDone();
                    printIndented("OK, I've marked this task as not done yet:");
                    printIndented("  " + tasks.get(unmarkIndex));
                    saveTasks(storage, tasks);
                    break;
                case DELETE:
                    int deleteIndex = parseTaskIndex(arguments, tasks.size());
                    Task removed = tasks.remove(deleteIndex);
                    printIndented("Noted. I've removed this task:");
                    printIndented("  " + removed);
                    printIndented("Now you have " + tasks.size() + " tasks in the list.");
                    saveTasks(storage, tasks);
                    break;
                case TODO:
                    if (arguments.trim().isEmpty()) {
                        throw new JohnnyException("The description of a todo cannot be empty.");
                    }
                    tasks.add(new Todo(arguments.trim()));
                    printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(storage, tasks);
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
                    printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(storage, tasks);
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
                    printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(storage, tasks);
                    break;
                }
                case UNKNOWN:
                    throw new JohnnyException("I'm sorry, but I'm not too sure what that means :(");
                }
            } catch (JohnnyException e) {
                printIndented("OOPS!!! " + e.getMessage());
            }
            printLine();
            if (input.split(" ", 2)[0].equalsIgnoreCase("bye")) {
                break;
            }
        }

        scanner.close();
    }
}
