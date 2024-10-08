
public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Task task1 = manager.createTask("Переезд", "Организовать переезд", Status.NEW);
        Epic epic = manager.createEpic("Организация праздника", "Организовать семейный праздник");
        Subtask subtask1 = manager.createSubtask("Пригласить гостей", "Собрать адреса гостей", Status.NEW, epic.getId());

        System.out.println("Все задачи:");
        System.out.println(manager.getAllTasks());

        System.out.println("Все эпики:");
        System.out.println(manager.getAllEpics());

        manager.deleteAllTasks();
        System.out.println("После удаления всех задач:");
        System.out.println(manager.getAllTasks());

        manager.deleteAllSubtasks();
        System.out.println("После удаления всех подзадач:");
        System.out.println(manager.getAllSubtasks());

        manager.deleteAllEpics();
        System.out.println("После удаления всех эпиков:");
        System.out.println(manager.getAllEpics());

    }
}


