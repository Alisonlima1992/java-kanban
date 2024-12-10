package test.com.practicum.service;

import com.practicum.model.Task;
import com.practicum.service.*;
import org.junit.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager = new InMemoryHistoryManager();
    private TaskManager taskManager = Managers.getDefault();

    @Test
    public void testAddTaskToHistory() {
        Task task = taskManager.createTask("Test Task", "Description", Status.NEW);
        historyManager.add(task);

        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void testRemoveTaskFromHistory() {
        Task task = taskManager.createTask("Test Task", "Description", Status.NEW);
        historyManager.add(task);
        historyManager.remove(task.getId());

        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void testPreventDuplicateHistory() {
        Task task = taskManager.createTask("Test Task", "Description", Status.NEW);
        historyManager.add(task);
        historyManager.add(task);

        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void testLastAddedTaskInHistory() {
        Task task1 = taskManager.createTask("Task 1", "Description 1", Status.NEW);
        Task task2 = taskManager.createTask("Task 2", "Description 2", Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(1));
        assertEquals(task1, history.get(0));
    }
}