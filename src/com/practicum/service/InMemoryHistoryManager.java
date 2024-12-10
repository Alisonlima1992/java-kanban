package com.practicum.service;

import com.practicum.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history = new ArrayList<>();

    public void add(Task task) {
        if (history.size() >= 10) {
            history.remove(0);
        }
        history.add(task);
    }

    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
