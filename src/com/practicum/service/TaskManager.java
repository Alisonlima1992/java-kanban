package com.practicum.service;

import com.practicum.model.*;
import java.util.ArrayList;

import java.time.Duration;
import java.time.LocalDateTime;

public interface TaskManager {
    Task createTask(String title, String description, Status status, Duration duration, LocalDateTime startTime);

    Subtask createSubtask(String title, String description, Status status, int epicId, Duration duration, LocalDateTime startTime);

    Epic createEpic(String title, String description);

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Task> getPrioritizedTasks();

    ArrayList<Subtask> getSubtasksForEpic(int epicId);

    ArrayList<Task> getHistory();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void updateTask(Task task);

    void deleteTask(int id);

    void deleteSubtask(int id);

    void deleteEpic(int id);

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    void deleteAll();

    void saveToFile(String filePath);

    void loadFromFile(String filePath);
}

