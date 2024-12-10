package test.com.practicum.service;

import com.practicum.model.Task;
import com.practicum.service.*;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    public void testGetDefaultReturnsInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager);
        assertTrue(taskManager instanceof InMemoryTaskManager);
    }

    @Test
    public void testGetDefaultHistoryReturnsInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "HistoryManager should not be null");
        assertTrue(historyManager instanceof InMemoryHistoryManager);
    }

    @Test
    public void testTaskManagerIsReadyToUse() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);

        Task task = taskManager.createTask("Test Task", "Test Description", Status.NEW);
        assertNotNull(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, retrievedTask);
    }

    @Test
    public void testHistoryManagerIsReadyToUse() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager should be initialized");

        Task task = new Task(1, "Example Task", "Example Description", Status.NEW);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));
    }
}