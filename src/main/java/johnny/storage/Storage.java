package johnny.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import johnny.task.Deadline;
import johnny.task.Event;
import johnny.task.Task;
import johnny.task.TaskList;
import johnny.task.Todo;

/**
 * Handles saving and loading tasks to/from a file on disk.
 * The file is stored at ./data/johnny.txt relative to the project root.
 */
public class Storage {

    private final Path filePath;

    /**
     * Creates a Storage instance that reads from and writes to the given file path.
     *
     * @param filePath path to the task data file (e.g., "./data/johnny.txt")
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the file. Returns an empty list if the file does not exist.
     * Each line is expected in the pipe-delimited format produced by toFileString().
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }
        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split(" \\| ");
            try {
                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];
                Task task;
                switch (type) {
                case "T":
                    task = new Todo(description);
                    break;
                case "D":
                    task = new Deadline(description, LocalDate.parse(parts[3]));
                    break;
                case "E":
                    task = new Event(description, LocalDate.parse(parts[3]),
                            LocalDate.parse(parts[4]));
                    break;
                default:
                    continue;
                }
                if (isDone) {
                    task.markAsDone();
                }
                tasks.add(task);
            } catch (ArrayIndexOutOfBoundsException | java.time.format.DateTimeParseException e) {
                // Skip corrupted lines (missing fields or unparseable dates)
                continue;
            }
        }
        return tasks;
    }

    /**
     * Saves all tasks to the file, creating the parent directory if needed.
     */
    public void save(TaskList tasks) throws IOException {
        Files.createDirectories(filePath.getParent());
        try (FileWriter fw = new FileWriter(filePath.toFile())) {
            for (Task task : tasks.getAll()) {
                fw.write(task.toFileString() + System.lineSeparator());
            }
        }
    }
}
