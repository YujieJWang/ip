package johnny.ui;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import johnny.task.Todo;

public class UiTest {

    @Test
    public void showTaskAdded_zeroTaskCount_throwsAssertionError() {
        Ui ui = new Ui(message -> { });

        assertThrows(AssertionError.class, () -> ui.showTaskAdded(new Todo("read book"), 0));
    }

    @Test
    public void showTaskDeleted_negativeTaskCount_throwsAssertionError() {
        Ui ui = new Ui(message -> { });

        assertThrows(AssertionError.class, () -> ui.showTaskDeleted(new Todo("read book"), -1));
    }
}
