package com.practicum.service;

import com.practicum.model.Epic;
import com.practicum.model.Subtask;
import com.practicum.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task createTask(String title, String description, Status status) {
        Task task = super.createTask(title, description, status);
        save();
        return task;
    }

    @Override
    public Subtask createSubtask(String title, String description, Status status, int epicId) {
        Subtask subtask = super.createSubtask(title, description, status, epicId);
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
            sb.append(toString(task)).append("\n");
        }

        for (Epic epic : getAllEpics()) {
            sb.append(toString(epic)).append("\n");
        }

        for (Subtask subtask : getAllSubtasks()) {
            sb.append(toString(subtask)).append("\n");
        }

        try {
            Files.writeString(file.toPath(), sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении менеджера в файл", e);
        }
    }

    private String toString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d",
                    task.getId(), task.getTitle(), task.getStatus(), task.getDescription(), subtask.getEpicId());
        } else if (task instanceof Epic) {
            return String.format("%d,EPIC,%s,%s,%s,http://", task.getId(), task.getTitle(), task.getStatus(), task.getDescription());
        } else {
            return String.format("%d,TASK,%s,%s,%s,http://", task.getId(), task.getTitle(), task.getStatus(), task.getDescription());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());

            FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
            for (String line : lines.subList(1, lines.size())) {
                Task task = fromString(line);
                if (task instanceof Epic) {
                    taskManager.createEpic(task.getTitle(), task.getDescription());
                } else if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    taskManager.createSubtask(subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
                } else {
                    taskManager.createTask(task.getTitle(), task.getDescription(), task.getStatus());
                }
            }
            return taskManager;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке менеджера из файла", e);
        }
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
