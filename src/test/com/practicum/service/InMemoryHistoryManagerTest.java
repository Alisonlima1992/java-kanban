package test.com.practicum.service;

import com.practicum.model.Task;
import com.practicum.service.*;
import org.junit.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager = new InMemoryHistoryManager();
    private TaskManager taskManager = Managers.getDefault();

    @Test
    public void testSetDescription() {
        Task task = taskManager.createTask("Test Task", "Old Description", Status.NEW);

        historyManager.add(task);

        task.setDescription("New Description");

        assertEquals("New Description", task.getDescription());
        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

}