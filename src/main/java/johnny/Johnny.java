package johnny;

import java.io.IOException;

import johnny.command.Command;
import johnny.command.Parser;
import johnny.storage.Storage;
import johnny.task.Task;
import johnny.task.TaskList;
import johnny.ui.Ui;

/**
 * Main application class for the Johnny chatbot.
 * Orchestrates the UI, storage, task list, and parser components.
 */
public class Johnny {

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Creates a Johnny instance, loading saved tasks from the given file path.
     * If the file cannot be read, starts with an empty task list.
     *
     * @param filePath path to the task data file (e.g., "./data/johnny.txt")
     */
    public Johnny(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (Exception e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    private void saveTasks() {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            ui.showSaveError();
        }
    }

    /** Runs the main command loop until the user says bye or input ends. */
    public void run() {
        ui.showGreeting();

        while (ui.hasNextLine()) {
            String input = ui.readCommand();
            ui.showLine();
            try {
                Command command = Parser.parseCommand(input);
                String arguments = Parser.parseArguments(input);

                switch (command) {
                case BYE:
                    ui.showFarewell();
                    break;
                case LIST:
                    ui.showTaskList(tasks);
                    break;
                case MARK:
                    int markIndex = Parser.parseTaskIndex(arguments, tasks.size());
                    tasks.get(markIndex).markAsDone();
                    ui.showTaskMarked(tasks.get(markIndex));
                    saveTasks();
                    break;
                case UNMARK:
                    int unmarkIndex = Parser.parseTaskIndex(arguments, tasks.size());
                    tasks.get(unmarkIndex).markAsNotDone();
                    ui.showTaskUnmarked(tasks.get(unmarkIndex));
                    saveTasks();
                    break;
                case DELETE:
                    int deleteIndex = Parser.parseTaskIndex(arguments, tasks.size());
                    Task removed = tasks.delete(deleteIndex);
                    ui.showTaskDeleted(removed, tasks.size());
                    saveTasks();
                    break;
                case TODO:
                    tasks.add(Parser.parseTodo(arguments));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks();
                    break;
                case DEADLINE:
                    tasks.add(Parser.parseDeadline(arguments));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks();
                    break;
                case EVENT:
                    tasks.add(Parser.parseEvent(arguments));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks();
                    break;
                case UNKNOWN:
                    throw new JohnnyException("I'm sorry, but I'm not too sure what that means :(");
                }
            } catch (JohnnyException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
            if (Parser.parseCommand(input) == Command.BYE) {
                break;
            }
        }

        ui.close();
    }

    /** Entry point for the Johnny chatbot application. */
    public static void main(String[] args) {
        new Johnny("./data/johnny.txt").run();
    }
}
