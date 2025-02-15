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
        super();
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

        sb.append("history\n");
        for (Task task : getHistory()) {
            sb.append(task.getId()).append(",");
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("\n");

        try {
            Files.writeString(file.toPath(), sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении менеджера в файл", e);
        }
    }

    private String toString(Task task) {
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

                if (task instanceof Epic) {
                    Epic epic = new Epic(taskManager.getNextId(), task.getTitle(), task.getDescription());
                    taskManager.epics.put(epic.getId(), epic);
                    taskManager.idCounter = Math.max(taskManager.idCounter, epic.getId() + 1);
                } else if (task instanceof Subtask) {
                    Subtask subtask = new Subtask(taskManager.getNextId(), task.getTitle(), task.getDescription(), task.getStatus(), ((Subtask) task).getEpicId());
                    taskManager.subtasks.put(subtask.getId(), subtask);
                    taskManager.idCounter = Math.max(taskManager.idCounter, subtask.getId() + 1);
                } else {
                    Task newTask = new Task(taskManager.getNextId(), task.getTitle(), task.getDescription(), task.getStatus());
                    taskManager.tasks.put(newTask.getId(), newTask);
                    taskManager.idCounter = Math.max(taskManager.idCounter, newTask.getId() + 1);
                }
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
