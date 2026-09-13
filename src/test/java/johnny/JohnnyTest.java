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
                "     Consider it noted:",
                "       [T][ ] read book",
                "     Your agenda now has 1 task."), messages);
    }

    @Test
    public void processCommand_bye_displaysFarewellAndExits() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        boolean shouldExit = johnny.processCommand("bye");

        assertTrue(shouldExit);
        assertTrue(messages.contains("     Until next time. I'll keep things in order."));
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
                "     Excellent. One task completed:",
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
                "     As you wish. The last change has been undone.",
                "     Here is your current agenda:"), messages);
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
                "     As you wish. The last change has been undone.",
                "     Here is your current agenda:",
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
                "     As you wish. The last change has been undone.",
                "     Here is your current agenda:",
                "     1.[T][ ] read book"), messages);
    }

    @Test
    public void processCommand_undoWithoutTaskChange_displaysError() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        johnny.processCommand("undo");

        assertEquals(List.of("     I'm afraid something is amiss: There is no command to undo."), messages);
    }

    @Test
    public void processCommand_unknownCommand_displaysButlerError() {
        List<String> messages = new ArrayList<>();
        Johnny johnny = new Johnny(tempDir.resolve("johnny.txt").toString(), new Ui(messages::add));

        johnny.processCommand("blah");

        assertEquals(List.of(
                "     I'm afraid something is amiss: I couldn't identify that command."), messages);
    }

    @Test
    public void constructor_directoryPath_displaysLoadingWarning() {
        List<String> messages = new ArrayList<>();

        new Johnny(tempDir.toString(), new Ui(messages::add));

        assertEquals(List.of(
                "     A note, if I may: I couldn't load your agenda, so we'll start afresh."), messages);
    }
}
