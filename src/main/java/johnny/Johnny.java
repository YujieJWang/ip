package johnny;

import java.io.IOException;

import johnny.command.Command;
import johnny.command.Parser;
import johnny.storage.Storage;
import johnny.task.Task;
import johnny.task.TaskList;
import johnny.ui.Ui;

/**
 * Coordinates the UI, storage, task list, and parser for the Johnny chatbot.
 */
public class Johnny {

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Creates a Johnny instance, loading saved tasks from the given file path.
     * If the file cannot be read, starts with an empty task list.
     *
     * @param filePath path to the task data file (e.g., "./data/johnny.txt").
     */
    public Johnny(String filePath) {
        this(filePath, new Ui());
    }

    /**
     * Creates a Johnny instance that sends messages through the supplied UI.
     *
     * @param filePath path to the task data file.
     * @param ui user interface used for input-independent output.
     */
    public Johnny(String filePath, Ui ui) {
        this.ui = ui;
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

    /**
     * Processes one command and displays the resulting response.
     *
     * @param input command entered by the user.
     * @return true if the command requests that the application exit.
     */
    public boolean processCommand(String input) {
        try {
            Command command = Parser.parseCommand(input);
            String arguments = Parser.parseArguments(input);

            switch (command) {
                case BYE:
                    ui.showFarewell();
                    return true;
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
                case FIND:
                    if (arguments.trim().isEmpty()) {
                        throw new JohnnyException("Please provide a keyword to search for.");
                    }
                    ui.showFindResults(tasks.find(arguments.trim()));
                    break;
                default:
                    throw new JohnnyException("I'm sorry, but I'm not too sure what that means :(");
            }
        } catch (JohnnyException e) {
            ui.showError(e.getMessage());
        }
        return false;
    }

    /**
     * Runs the console command loop until the user says bye or input ends.
     */
    public void run() {
        ui.showGreeting();

        while (ui.hasNextLine()) {
            String input = ui.readCommand();
            ui.showLine();
            boolean shouldExit = processCommand(input);
            ui.showLine();
            if (shouldExit) {
                break;
            }
        }

        ui.close();
    }

    /**
     * Starts the console version of Johnny.
     *
     * @param args command-line arguments, which are unused.
     */
    public static void main(String[] args) {
        new Johnny("./data/johnny.txt").run();
    }
}
