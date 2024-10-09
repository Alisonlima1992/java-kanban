package com.practicum.model;
import com.practicum.service.Status;
public class Subtask extends Task{
    private final int epicId;

    public Subtask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}


