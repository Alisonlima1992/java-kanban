package test.com.practicum.service;

import org.junit.Test;
import com.practicum.model.*;
import com.practicum.service.*;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void testAddTasksAndFindById() {
        Task task = taskManager.createTask("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Epic epic = taskManager.createEpic("Epic 1", "Epic Description");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Subtask Description", Status.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.now());

        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void testIdCollision() {
        Task task1 = taskManager.createTask("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = taskManager.createTask("Task 2", "Description 2", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    public void testDeleteTaskRemovesFromHistory() {
        Task task = taskManager.createTask("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.deleteTask(task.getId());

        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    public void testDeleteSubtaskRemovesFromHistory() {
        Epic epic = taskManager.createEpic("Epic 1", "Epic Description");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Subtask Description", Status.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.now());
        taskManager.deleteSubtask(subtask.getId());

        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    public void testUpdateTaskUpdatesHistory() {
        Task task = taskManager.createTask("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.getTaskById(task.getId());
        task.setDescription("Updated Description");

        assertEquals("Updated Description", task.getDescription());
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    public void testCreateTask() {
        Task task = taskManager.createTask("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        assertNotNull(task);
        assertEquals("Task 1", task.getTitle());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    public void testCreateSubtask() {
        Epic epic = taskManager.createEpic("Epic 1", "Description of epic 1");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Description of subtask 1", Status.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.now());

        assertNotNull(subtask);
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(Status.NEW, subtask.getStatus());

    }

    @Test
    public void testUpdateEpicStatus() {
        Epic epic = taskManager.createEpic("Epic 1", "Description of epic 1");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Description of subtask 1", Status.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.now());
        taskManager.updateTask(subtask);

        assertEquals(Status.NEW, epic.getStatus());

        subtask.setStatus(Status.DONE);
        taskManager.updateTask(subtask);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void testDeleteTask() {
        Task task = taskManager.createTask("Task 2", "Description 2", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.deleteTask(task.getId());
        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    public void testDeleteEpic() {
        Epic epic = taskManager.createEpic("Epic 2", "Description of epic 2");
        taskManager.createSubtask("Subtask 2", "Description of subtask 2", Status.NEW, epic.getId(), Duration.ofMinutes(20), LocalDateTime.now());
        taskManager.deleteEpic(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()));
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    public void testGetHistory() {
        Task task1 = taskManager.createTask("Task 3", "Description 3", Status.NEW, Duration.ofMinutes(25), LocalDateTime.now());
        taskManager.getTaskById(task1.getId());
        Task task2 = taskManager.createTask("Task 4", "Description 4", Status.NEW, Duration.ofMinutes(45), LocalDateTime.now());
        taskManager.getTaskById(task2.getId());

        assertEquals(2, taskManager.getHistory().size());
        assertTrue(taskManager.getHistory().contains(task1));
        assertTrue(taskManager.getHistory().contains(task2));
    }

    @Test
    public void testPrioritizedTasks() {
        Task task1 = taskManager.createTask("Task 5", "Description 5", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(30));
        Task task2 = taskManager.createTask("Task 6", "Description 6", Status.NEW, Duration.ofMinutes(20), LocalDateTime.now().plusMinutes(20));

        assertEquals(task2, taskManager.getPrioritizedTasks().get(0)); // Проверяем, что task2 с приоритетом
        assertEquals(task1, taskManager.getPrioritizedTasks().get(1)); // Проверяем, что task1 с приоритетом
    }
}