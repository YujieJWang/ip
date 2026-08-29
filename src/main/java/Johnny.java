import java.io.IOException;

public class Johnny {

    private static void saveTasks(Storage storage, TaskList tasks, Ui ui) {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            ui.showSaveError();
        }
    }

    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("./data/johnny.txt");
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
        } catch (Exception e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }

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
                    saveTasks(storage, tasks, ui);
                    break;
                case UNMARK:
                    int unmarkIndex = Parser.parseTaskIndex(arguments, tasks.size());
                    tasks.get(unmarkIndex).markAsNotDone();
                    ui.showTaskUnmarked(tasks.get(unmarkIndex));
                    saveTasks(storage, tasks, ui);
                    break;
                case DELETE:
                    int deleteIndex = Parser.parseTaskIndex(arguments, tasks.size());
                    Task removed = tasks.delete(deleteIndex);
                    ui.showTaskDeleted(removed, tasks.size());
                    saveTasks(storage, tasks, ui);
                    break;
                case TODO:
                    tasks.add(Parser.parseTodo(arguments));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(storage, tasks, ui);
                    break;
                case DEADLINE:
                    tasks.add(Parser.parseDeadline(arguments));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(storage, tasks, ui);
                    break;
                case EVENT:
                    tasks.add(Parser.parseEvent(arguments));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(storage, tasks, ui);
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
}
