package test.com.practicum.service;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import com.practicum.model.*;
import com.practicum.service.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager = new InMemoryTaskManager();


    @Test
    public void testAddTasksAndFindById() {
        // Создаем задачи разного типа
        Task task = taskManager.createTask("Task 1", "Description 1", Status.NEW);
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Subtask Description", Status.NEW, task.getId());
        Epic epic = taskManager.createEpic("Epic 1", "Epic Description");

        // Проверяем добавленные задачи по ID
        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void testIdCollision() {
        // Создаем задачи и сохраняем их
        Task task1 = taskManager.createTask("Task 1", "Description 1", Status.NEW);
        Task task2 = taskManager.createTask("Task 2", "Description 2", Status.NEW);

        // Проверяем, что ID разные
        assertNotEquals(task1.getId(), task2.getId());
    }

}