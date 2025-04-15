package com.practicum.model;

import com.practicum.service.Status;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private final int id;
    private final String title;
    private String description;
    private Status status;

    private Duration duration;
    private LocalDateTime startTime;

    public Task(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return String.format(
                "Task{id=%d, title='%s', description='%s', status=%s, duration=%d minutes, startTime=%s}",
                id, title, description, status, duration != null ? duration.toMinutes() : 0,
                startTime != null ? startTime.toString() : "null"
        );
    }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Task)) return false;
            Task task = (Task) o;
            return id == task.id;
    }

    @Override
    public int hashCode() {
            return id;
    }

}
