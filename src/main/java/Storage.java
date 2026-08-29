import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Handles saving tasks to a file on disk.
 * The file is written to ./data/johnny.txt relative to the project root.
 */
public class Storage {

    private final Path filePath;

    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Saves all tasks to the file, creating the parent directory if needed.
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        Files.createDirectories(filePath.getParent());
        FileWriter fw = new FileWriter(filePath.toFile());
        for (Task task : tasks) {
            fw.write(task.toFileString() + System.lineSeparator());
        }
        fw.close();
    }
}
