package johnny.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import johnny.JohnnyException;
import johnny.task.Deadline;
import johnny.task.Event;
import johnny.task.Todo;

public class ParserTest {

    // --- parseCommand ---

    @Test
    public void parseCommand_validCommand_correctEnum() {
        assertEquals(Command.TODO, Parser.parseCommand("todo read book"));
    }

    @Test
    public void parseCommand_caseInsensitive_correctEnum() {
        assertEquals(Command.TODO, Parser.parseCommand("ToDo read book"));
    }

    @Test
    public void parseCommand_unknownCommand_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Parser.parseCommand("blah"));
    }

    @Test
    public void parseCommand_emptyInput_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Parser.parseCommand(""));
    }

    // --- parseArguments ---

    @Test
    public void parseArguments_hasArguments_returnsArguments() {
        assertEquals("read book", Parser.parseArguments("todo read book"));
    }

    @Test
    public void parseArguments_noArguments_returnsEmpty() {
        assertEquals("", Parser.parseArguments("list"));
    }

    // --- parseTaskIndex ---

    @Test
    public void parseTaskIndex_validIndex_returnsZeroBased() throws JohnnyException {
        assertEquals(1, Parser.parseTaskIndex("2", 3));
    }

    @Test
    public void parseTaskIndex_emptyArguments_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseTaskIndex("", 3));
    }

    @Test
    public void parseTaskIndex_nonNumeric_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseTaskIndex("abc", 3));
    }

    @Test
    public void parseTaskIndex_outOfRangeHigh_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseTaskIndex("5", 3));
    }

    @Test
    public void parseTaskIndex_outOfRangeLow_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseTaskIndex("0", 3));
    }

    // --- parseTodo ---

    @Test
    public void parseTodo_validDescription_returnsTodo() throws JohnnyException {
        Todo todo = Parser.parseTodo("read book");
        assertTrue(todo.toString().contains("read book"));
    }

    @Test
    public void parseTodo_emptyDescription_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseTodo_whitespaceOnly_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseTodo("   "));
    }

    // --- parseDeadline ---

    @Test
    public void parseDeadline_validInput_returnsDeadline() throws JohnnyException {
        Deadline deadline = Parser.parseDeadline("homework /by 2024-09-15");
        assertTrue(deadline.toString().contains("homework"));
        assertTrue(deadline.toString().contains("Sept 15 2024")
                || deadline.toString().contains("Sep 15 2024"));
    }

    @Test
    public void parseDeadline_missingByDelimiter_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseDeadline("homework"));
    }

    @Test
    public void parseDeadline_emptyDescription_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseDeadline(" /by 2024-09-15"));
    }

    @Test
    public void parseDeadline_emptyDate_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseDeadline("homework /by "));
    }

    @Test
    public void parseDeadline_invalidDateFormat_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseDeadline("homework /by Sunday"));
    }

    // --- parseEvent ---

    @Test
    public void parseEvent_validInput_returnsEvent() throws JohnnyException {
        Event event = Parser.parseEvent("meeting /from 2024-03-01 /to 2024-03-03");
        assertTrue(event.toString().contains("meeting"));
        assertTrue(event.toString().contains("Mar 01 2024"));
        assertTrue(event.toString().contains("Mar 03 2024"));
    }

    @Test
    public void parseEvent_missingFromDelimiter_throwsException() {
        assertThrows(JohnnyException.class,
                () -> Parser.parseEvent("meeting /to 2024-03-03"));
    }

    @Test
    public void parseEvent_missingToDelimiter_throwsException() {
        assertThrows(JohnnyException.class,
                () -> Parser.parseEvent("meeting /from 2024-03-01"));
    }

    @Test
    public void parseEvent_invalidDateFormat_throwsException() {
        assertThrows(JohnnyException.class,
                () -> Parser.parseEvent("meeting /from Monday /to Friday"));
    }

    @Test
    public void parseEvent_fromAfterTo_throwsException() {
        assertThrows(JohnnyException.class,
                () -> Parser.parseEvent("meeting /to 2024-03-01 /from 2024-03-03"));
    }
}
