import java.io.IOException;
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();
        Storage storage = new Storage("./data/johnny.txt");

        printLine();
        System.out.print(BANNER);
        printIndented("Hello! I'm Johnny.");
        printIndented("What can I do for you?");
        printLine();

        while (true) {
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
                    int markIndex = Integer.parseInt(arguments) - 1;
                    tasks.get(markIndex).markAsDone();
                    printIndented("Nice! I've marked this task as done:");
                    printIndented("  " + tasks.get(markIndex));
                    saveTasks(storage, tasks);
                    break;
                case UNMARK:
                    int unmarkIndex = Integer.parseInt(arguments) - 1;
                    tasks.get(unmarkIndex).markAsNotDone();
                    printIndented("OK, I've marked this task as not done yet:");
                    printIndented("  " + tasks.get(unmarkIndex));
                    saveTasks(storage, tasks);
                    break;
                case DELETE:
                    int deleteIndex = Integer.parseInt(arguments) - 1;
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
                    tasks.add(new Todo(arguments));
                    printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(storage, tasks);
                    break;
                case DEADLINE:
                    int byIndex = arguments.indexOf(" /by ");
                    String deadlineDesc = arguments.substring(0, byIndex);
                    String by = arguments.substring(byIndex + 5);
                    tasks.add(new Deadline(deadlineDesc, by));
                    printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(storage, tasks);
                    break;
                case EVENT:
                    int fromIndex = arguments.indexOf(" /from ");
                    int toIndex = arguments.indexOf(" /to ");
                    String eventDesc = arguments.substring(0, fromIndex);
                    String from = arguments.substring(fromIndex + 7, toIndex);
                    String to = arguments.substring(toIndex + 5);
                    tasks.add(new Event(eventDesc, from, to));
                    printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(storage, tasks);
                    break;
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
