package com.practicum.model;

import com.practicum.service.Status;
import java.util.Objects;


public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public static Subtask createSubtask(int id, String title, String description, Status status, int epicId) {

        if (id == epicId) {
            System.out.println("Не удается создать подзадачу. epicId совпадает с id.");
            return null;
        }
        return new Subtask(id, title, description, status, epicId);
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return String.format("Subtask{id=%d, title='%s', description='%s', status=%s, epicId=%d, duration=%d minutes, startTime=%s}",
                getId(), getTitle(), getDescription(), getStatus(), epicId,
                getDuration() != null ? getDuration().toMinutes() : 0,
                getStartTime() != null ? getStartTime().toString() : "null");
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return true;
    }

    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
