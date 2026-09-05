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
}
