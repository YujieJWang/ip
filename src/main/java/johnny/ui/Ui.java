package johnny.ui;

import java.util.Scanner;

import johnny.task.Task;
import johnny.task.TaskList;

/**
 * Handles all interactions with the user, including reading input
 * and displaying formatted output.
 */
public class Ui {

    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "     _       _                       \n"
            + "    | | ___ | |__  _ __  _ __  _   _ \n"
            + " _  | |/ _ \\| '_ \\| '_ \\| '_ \\| | | |\n"
            + "| |_| | (_) | | | | | | | | | | |_| |\n"
            + " \\___/ \\___/|_| |_|_| |_|_| |_|\\__, |\n"
            + "                                |___/ \n";

    private final Scanner scanner;

    /** Creates a Ui that reads from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Returns true if there is another line of user input available. */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /** Reads and returns the next line of user input. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Closes the underlying input scanner. */
    public void close() {
        scanner.close();
    }

    /** Prints a horizontal divider line. */
    public void showLine() {
        System.out.println("    " + LINE);
    }

    private void printIndented(String message) {
        System.out.println("     " + message);
    }

    /** Displays the welcome banner and greeting message. */
    public void showGreeting() {
        showLine();
        System.out.print(BANNER);
        printIndented("Hello! I'm Johnny.");
        printIndented("What can I do for you?");
        showLine();
    }

    public void showFarewell() {
        printIndented("Bye bye! See you again soon.");
    }

    public void showLoadingError() {
        printIndented("Warning: Could not load saved tasks. Starting with an empty list.");
    }

    public void showSaveError() {
        printIndented("Warning: Could not save tasks to disk.");
    }

    public void showError(String message) {
        printIndented("OOPS!!! " + message);
    }

    /** Displays a confirmation message after a task is added. */
    public void showTaskAdded(Task task, int taskCount) {
        printIndented("Got it. I've added this task:");
        printIndented("  " + task);
        printIndented("Now you have " + taskCount + " tasks in the list.");
    }

    public void showTaskMarked(Task task) {
        printIndented("Nice! I've marked this task as done:");
        printIndented("  " + task);
    }

    public void showTaskUnmarked(Task task) {
        printIndented("OK, I've marked this task as not done yet:");
        printIndented("  " + task);
    }

    public void showTaskDeleted(Task task, int taskCount) {
        printIndented("Noted. I've removed this task:");
        printIndented("  " + task);
        printIndented("Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays all tasks in the list, numbered starting from 1. */
    public void showTaskList(TaskList tasks) {
        printIndented("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            printIndented((i + 1) + "." + tasks.get(i));
        }
    }
}
