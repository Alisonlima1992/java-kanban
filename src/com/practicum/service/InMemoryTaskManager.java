package com.practicum.service;


import com.practicum.model.Task;
import com.practicum.model.Epic;
import com.practicum.model.Subtask;

import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private final ArrayList<Task> history = new ArrayList<>();

    private int idCounter = 1;

    public Task createTask(String title, String description, Status status) {
        Task task = new Task(idCounter++, title, description, status);
        tasks.put(task.getId(), task);
        return task;
    }

    public Subtask createSubtask(String title, String description, Status status, int epicId) {
        Subtask subtask = new Subtask(idCounter++, title, description, status, epicId);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(epicId);

        if (epic != null) {
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
        }
        return subtask;
    }

    public Epic createEpic(String title, String description) {
        Epic epic = new Epic(idCounter++, title, description);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void updateTask(Task task) {

        if (task instanceof Epic) {
            epics.put(task.getId(), (Epic) task);
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        } else {
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(subtask);
                updateEpicStatus(epic);
            }
            subtasks.remove(id);
        }
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                deleteSubtask(subtask.getId());
            }
            updateEpicStatus(epic);
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        for (Subtask subtask : new ArrayList<>(subtasks.values())) {
            deleteSubtask(subtask.getId());
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    public void deleteAll() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    public ArrayList<Subtask> getSubtasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic != null ? epic.getSubtasks() : new ArrayList<>();
    }

    private void updateEpicStatus(Epic epic) {
        epic.updateStatus();
    }


    public ArrayList<Task> getHistory() {
        return history;
    }

    private void addToHistory(Task task) {
        if (history.size() >= 10) {
            history.remove(0);
        }
        history.add(task);
    }

}

