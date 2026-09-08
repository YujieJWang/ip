package johnny;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import johnny.ui.Ui;

public class JohnnyTest {

    @TempDir
    Path tempDir;

    @Test
    public void processCommand_todo_displaysResponseAndContinues() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        boolean shouldExit = johnny.processCommand("todo read book");

        assertFalse(shouldExit);
        assertEquals(List.of(
                "     Got it. I've added this task:",
                "       [T][ ] read book",
                "     Now you have 1 tasks in the list."), messages);
    }

    @Test
    public void processCommand_bye_displaysFarewellAndExits() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        boolean shouldExit = johnny.processCommand("bye");

        assertTrue(shouldExit);
        assertTrue(messages.contains("     Bye bye! See you again soon."));
    }

    @Test
    public void processCommand_mark_displaysMarkedTaskAndContinues() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo read book");
        messages.clear();

        boolean shouldExit = johnny.processCommand("mark 1");

        assertFalse(shouldExit);
        assertEquals(List.of(
                "     Nice! I've marked this task as done:",
                "       [T][X] read book"), messages);
    }

    @Test
    public void processCommand_undoAddedTask_removesTask() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo read book");
        messages.clear();

        johnny.processCommand("undo");
        johnny.processCommand("list");

        assertEquals(List.of(
                "     Done! I've undone the last command.",
                "     Here are the tasks in your list:"), messages);
    }

    @Test
    public void processCommand_undoDeletedTask_restoresTaskAtOriginalPosition() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo first");
        johnny.processCommand("todo second");
        johnny.processCommand("delete 1");
        messages.clear();

        johnny.processCommand("undo");
        johnny.processCommand("list");

        assertEquals(List.of(
                "     Done! I've undone the last command.",
                "     Here are the tasks in your list:",
                "     1.[T][ ] first",
                "     2.[T][ ] second"), messages);
    }

    @Test
    public void processCommand_undoMarkedTask_restoresPreviousStatus() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));
        johnny.processCommand("todo read book");
        johnny.processCommand("mark 1");
        messages.clear();

        johnny.processCommand("undo");
        johnny.processCommand("list");

        assertEquals(List.of(
                "     Done! I've undone the last command.",
                "     Here are the tasks in your list:",
                "     1.[T][ ] read book"), messages);
    }

    @Test
    public void processCommand_undoWithoutTaskChange_displaysError() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        johnny.processCommand("undo");

        assertEquals(List.of("     OOPS!!! There is no command to undo."), messages);
    }

    @Test
    public void constructor_directoryPath_displaysLoadingWarning() {
        List<String> messages = new ArrayList<>();

        new Johnny(tempDir.toString(), new Ui(messages::add));

        assertEquals(List.of(
                "     Warning: Could not load saved tasks. Starting with an empty list."), messages);
    }
}
