package test.com.practicum.model;

import org.junit.Test;

import com.practicum.service.*;
import com.practicum.model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TaskTest {
    @Test
    public void testTasksAreEqualIfIdsAreEqual() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        Task task2 = new Task(1, "Task 2", "Description 2", Status.NEW);

        assertEquals(task1, task2);
    }

    @Test
    public void testTasksAreNotEqualIfIdsAreDifferent() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW);

        assertNotEquals(task1, task2);
    }

    @Test
    public void testSubtasksAreEqualIfIdsAreEqual() {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, 1);
        Subtask subtask2 = new Subtask(1, "Subtask 2", "Description 2", Status.NEW, 2);

        assertEquals(subtask1, subtask2);
    }

    @Test
    public void testEpicsAreEqualIfIdsAreEqual() {
        Epic epic1 = new Epic(2, "Epic 1", "Description 1");
        Epic epic2 = new Epic(2, "Epic 2", "Description 2");

        assertEquals(epic1, epic2);
    }

    @Test
    public void testSubtasksAreDifferentClasses() {
        Epic epic = new Epic(1, "Epic Title", "Epic Description");
        Subtask subtask = new Subtask(2, "Subtask Title", "Subtask Description", Status.NEW, epic.getId());

        assertNotEquals(epic, subtask);
    }

    @Test
    public void testSubtasksAreNotEqualIfIdsAreDifferent() {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, 1);
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", Status.NEW, 2);

        assertNotEquals(subtask1, subtask2);
    }

    @Test
    public void testEpicsAreNotEqualIfIdsAreDifferent() {
        Epic epic1 = new Epic(1, "Epic 1", "Description 1");
        Epic epic2 = new Epic(2, "Epic 2", "Description 2");

        assertNotEquals(epic1, epic2);
    }
}
