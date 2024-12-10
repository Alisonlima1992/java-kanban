package com.practicum.service;

import com.practicum.model.*;
import java.util.ArrayList;

public interface TaskManager {
    Task createTask(String title, String description, Status status);

    Subtask createSubtask(String title, String description, Status status, int epicId);

    Epic createEpic(String title, String description);

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Task> getHistory();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void deleteTask(int id);

    void deleteSubtask(int id);
}
