package com.practicum.service;



import com.practicum.model.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if (task1.getStartTime() == null && task2.getStartTime() != null) return 1;
        if (task1.getStartTime() != null && task2.getStartTime() == null) return -1;
        if (task1.getStartTime() == null && task2.getStartTime() == null) return Integer.compare(task1.getId(), task2.getId());
        return task1.getStartTime().compareTo(task2.getStartTime());
    });

    protected int idCounter = 1;

    @Override
    public Task createTask(String title, String description, Status status,Duration duration, LocalDateTime startTime) {
        Task task = new Task(idCounter++, title, description, status);
        task.setDuration(duration);
        task.setStartTime(startTime);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }
    @Override
    public Subtask createSubtask(String title, String description, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        Subtask subtask = new Subtask(idCounter++, title, description, status, epicId);
        subtask.setDuration(duration);
        subtask.setStartTime(startTime);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
            prioritizedTasks.add(subtask);
        }
        return subtask;
    }

    public Epic createEpic(String title, String description) {
        Epic epic = new Epic(idCounter++, title, description);
        epics.put(epic.getId(), epic);
        prioritizedTasks.add(epic);
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
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void updateTask(Task task) {
        prioritizedTasks.remove(task);

        if (task instanceof Epic) {
            epics.put(task.getId(), (Epic) task);
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.recalculateFields();
                epic.updateStatus();
            }
        } else {
            tasks.put(task.getId(), task);
        }

        prioritizedTasks.add(task);
    }

    public void deleteTask(int id) {

        Task task = tasks.remove(id);
        if (task != null) {
            historyManager.remove(id);
            prioritizedTasks.remove(task);
        }

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
            historyManager.remove(id);
        }
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                deleteSubtask(subtask.getId());
            }
            updateEpicStatus(epic);
            historyManager.remove(id);
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
        prioritizedTasks.clear();
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
        prioritizedTasks.clear();
    }

    public ArrayList<Subtask> getSubtasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic != null ? epic.getSubtasks() : new ArrayList<>();
    }

    private void updateEpicStatus(Epic epic) {
        epic.recalculateFields();
    }


    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean hasConflicts(Task newTask) {
        return prioritizedTasks.stream().anyMatch(existing -> isOverlapping(existing, newTask));
    }

    private boolean isOverlapping(Task existingTask, Task newTask) {
        LocalDateTime existingStart = existingTask.getStartTime();
        LocalDateTime existingEnd = existingTask.getEndTime();
        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();

        if (existingStart == null || existingEnd == null || newStart == null || newEnd == null) {
            return false;
        }

        return !(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd));
    }

    public void saveToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : tasks.values()) {
                writer.write(String.format("%d,%s,%s,%s,%d,%s",
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getDuration() != null ? task.getDuration().toMinutes() : 0,
                        task.getStartTime() != null ? task.getStartTime().toString() : "null"
                ));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл", e);
        }
    }

    public void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int id = Integer.parseInt(fields[0]);
                String title = fields[1];
                String description = fields[2];
                Status status = Status.valueOf(fields[3]);
                Duration duration = Duration.ofMinutes(Long.parseLong(fields[4]));
                LocalDateTime startTime = fields[5].equals("null") ? null : LocalDateTime.parse(fields[5]);

                Task task = new Task(id, title, description, status);
                task.setDuration(duration);
                task.setStartTime(startTime);
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла", e);
        }
    }
}
