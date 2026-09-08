package johnny.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
            try {
                tasks.add(parseTask(line));
            } catch (ArrayIndexOutOfBoundsException
                    | DateTimeParseException
                    | IllegalArgumentException e) {
                // Keep valid saved tasks usable even when one record is malformed.
                continue;
            }
        }
        return tasks;
    }

    /**
     * Parses one pipe-delimited task record from storage.
     *
     * @param line serialized task record
     * @return task represented by the record
     */
    private Task parseTask(String line) {
        String[] fields = line.split(" \\| ");
        String taskType = fields[0];
        boolean isDone = fields[1].equals("1");
        String description = fields[2];
        Task task;
        switch (taskType) {
            case "T":
                task = new Todo(description);
                break;
            case "D":
                task = new Deadline(description, LocalDate.parse(fields[3]));
                break;
            case "E":
                task = new Event(description, LocalDate.parse(fields[3]),
                        LocalDate.parse(fields[4]));
                break;
            default:
                throw new IllegalArgumentException("Unknown task type: " + taskType);
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Saves all tasks to the file, creating the parent directory if needed.
     */
    public void save(TaskList tasks) throws IOException {
        Files.createDirectories(filePath.getParent());
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            for (Task task : tasks.getAll()) {
                writer.write(task.toFileString() + System.lineSeparator());
            }
        }
    }
}
