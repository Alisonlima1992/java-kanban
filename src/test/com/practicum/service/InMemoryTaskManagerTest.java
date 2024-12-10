package test.com.practicum.service;

import org.junit.Test;
import com.practicum.model.*;
import com.practicum.service.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void testAddTasksAndFindById() {
        Task task = taskManager.createTask("Task 1", "Description 1", Status.NEW);
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Subtask Description", Status.NEW, task.getId());
        Epic epic = taskManager.createEpic("Epic 1", "Epic Description");

        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void testIdCollision() {
        Task task1 = taskManager.createTask("Task 1", "Description 1", Status.NEW);
        Task task2 = taskManager.createTask("Task 2", "Description 2", Status.NEW);

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    public void testDeleteTaskRemovesFromHistory() {
        Task task = taskManager.createTask("Task 1", "Description 1", Status.NEW);
        taskManager.deleteTask(task.getId());

        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    public void testDeleteSubtaskRemovesFromHistory() {
        Epic epic = taskManager.createEpic("Epic 1", "Epic Description");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Subtask Description", Status.NEW, epic.getId());
        taskManager.deleteSubtask(subtask.getId());

        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    public void testUpdateTaskUpdatesHistory() {
        Task task = taskManager.createTask("Task 1", "Description 1", Status.NEW);
        taskManager.getTaskById(task.getId());
        task.setDescription("Updated Description");

        assertEquals("Updated Description", task.getDescription());
        assertEquals(1, taskManager.getHistory().size());
    }
}