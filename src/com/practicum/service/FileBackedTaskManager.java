package com.practicum.service;

import com.practicum.model.Epic;
import com.practicum.model.Subtask;
import com.practicum.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    @Override
    public Task createTask(String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        Task task = super.createTask(title, description, status, duration, startTime);
        save();
        return task;
    }

    @Override
    public Subtask createSubtask(String title, String description, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        Subtask subtask = super.createSubtask(title, description, status, epicId, duration, startTime);
        save();
        return subtask;
    }

    @Override
    public Epic createEpic(String title, String description) {
        Epic epic = super.createEpic(title, description);
        save();
        return epic;
    }

    private void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,epic\n");

        for (Task task : getAllTasks()) {
            sb.append(taskToString(task)).append("\n");
        }

        for (Epic epic : getAllEpics()) {
            sb.append(taskToString(epic)).append("\n");
        }

        for (Subtask subtask : getAllSubtasks()) {
            sb.append(taskToString(subtask)).append("\n");
        }

        sb.append("history\n");
        String history = getHistory().stream()
                .map(task -> String.valueOf(task.getId()))
                .collect(Collectors.joining(","));
        sb.append(history).append("\n");

        try {
            Files.writeString(file.toPath(), sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении менеджера в файл", e);
        }
    }

    private String taskToString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.join(",",
                    String.valueOf(task.getId()),
                    "SUBTASK",
                    task.getTitle(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    String.valueOf(subtask.getEpicId())
            );
        } else if (task instanceof Epic) {
            return String.join(",",
                    String.valueOf(task.getId()),
                    "EPIC",
                    task.getTitle(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    ""
            );
        } else {
            return String.join(",",
                    String.valueOf(task.getId()),
                    "TASK",
                    task.getTitle(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    ""
            );
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());

            FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

            for (String line : lines.subList(1, lines.size())) {
                if (line.equals("history")) break;
                Task task = fromString(line);
                if (task instanceof Epic epic) {
                    taskManager.epics.put(epic.getId(), epic);
                } else if (task instanceof Subtask subtask) {
                    taskManager.subtasks.put(subtask.getId(), subtask);
                } else {
                    taskManager.tasks.put(task.getId(), task);
                }
                taskManager.idCounter = Math.max(taskManager.idCounter, task.getId() + 1);
            }

            int historyIndex = lines.indexOf("history");
            if (historyIndex >= 0 && historyIndex + 1 < lines.size()) {
                String historyLine = lines.get(historyIndex + 1);
                String[] historyIds = historyLine.split(",");
                for (String id : historyIds) {
                    if (!id.isEmpty()) {
                        taskManager.getHistory().add(taskManager.getTaskById(Integer.parseInt(id.trim())));
                    }
                }
            }

            return taskManager;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке менеджера из файла", e);
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка при обработке файла", e);
        }
    }

    private int getNextId() {
        return idCounter++;
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case "TASK":
                return new Task(id, name, description, status);
            case "EPIC":
                return new Epic(id, name, description);
            case "SUBTASK":
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                throw new IllegalArgumentException("Неверный тип задачи: " + type);
        }
    }
}
