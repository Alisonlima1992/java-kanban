package test.com.practicum.model;

import org.junit.Test;
import com.practicum.service.*;
import com.practicum.model.*;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    @Test
    public void testTasksAreEqualIfIdsAreEqual() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        Task task2 = new Task(1, "Task 2", "Description 2", Status.NEW);

        assertEquals(task1, task2, "Tasks should be equal if IDs are the same");
    }

    @Test
    public void testSubtasksAreEqualIfIdsAreEqual() {
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", Status.NEW, 1);
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", Status.NEW, 1);

        assertEquals(subtask1, subtask2, "Subtasks should be equal if IDs are the same");
    }

    @Test
    public void testEpicsAreEqualIfIdsAreEqual() {
        Epic epic1 = new Epic(3, "Epic 1", "Description 1");
        Epic epic2 = new Epic(3, "Epic 2", "Description 2");

        assertEquals(epic1, epic2, "Epics should be equal if IDs are the same");
    }

    @Test
    public void testEpicCannotAddItselfAsSubtask() {
        Epic epic = new Epic(1, "Epic Title", "Epic Description");
        Subtask subtask = new Subtask(2, "Subtask Title", "Subtask Description", Status.NEW, epic.getId());
        int expectedSize = epic.getSubtasks().size();
        epic.addSubtask(subtask);
        int actualSize = epic.getSubtasks().size();
        assertNotEquals(expectedSize, actualSize);
    }
}
