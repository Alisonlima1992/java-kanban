package com.practicum.model;


import com.practicum.service.Status;
import java.util.ArrayList;

import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    private LocalDateTime endTime;

    public Epic(int id, String title, String description) {
        super(id, title, description, Status.NEW);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
            if (subtask.getId() == this.getId()) {
                return;
            }

            for (Subtask s : subtasks) {
                if (s.getId() == subtask.getId()) {
                    return;
                }
            }

            if (subtask.getEpicId() == this.getId()) {
                subtasks.add(subtask);
                recalculateFields();
                updateStatus();
            }
        }


    public void removeSubtask(Subtask subtask) {
        this.subtasks.remove(subtask);
        recalculateFields();
    }

    public void recalculateFields() {
        if (subtasks.isEmpty()) {
            setDuration(Duration.ZERO);
            setStartTime(null);
            this.endTime = null;
            return;
        }


        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Subtask subtask : subtasks) {
            totalDuration = totalDuration.plus(subtask.getDuration());

            if (subtask.getStartTime() != null) {
                if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
                }
                if (latestEnd == null || subtask.getEndTime().isAfter(latestEnd)) {
                    latestEnd = subtask.getEndTime();
                }
            }
        }

        setDuration(totalDuration);
        setStartTime(earliestStart);
        this.endTime = latestEnd;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }
        boolean allDone = true;
        boolean anyInProgress = false;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            setStatus(Status.DONE);
        } else if (anyInProgress) {
            setStatus(Status.IN_PROGRESS);
        } else {
            setStatus(Status.NEW);
        }
    }

    @Override
    public String toString() {
        return String.format("Epic{id=%d, title='%s', description='%s', status=%s, duration=%d minutes, startTime=%s, endTime=%s}",
                getId(), getTitle(), getDescription(), getStatus(),
                getDuration() != null ? getDuration().toMinutes() : 0,
                getStartTime() != null ? getStartTime().toString() : "null",
                getEndTime() != null ? getEndTime().toString() : "null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        return super.equals(o);
    }

}

