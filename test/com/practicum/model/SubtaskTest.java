package test.com.practicum.model;

import com.practicum.model.Subtask;
import com.practicum.service.Status;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    public void testSubtaskCannotBeItsOwnEpic() {
        Subtask subtask = Subtask.createSubtask(1, "Subtask Title", "Subtask Description", Status.NEW, 1);

        assertNull(subtask);
    }
}