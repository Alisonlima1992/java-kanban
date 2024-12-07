package com.practicum.service;

import com.practicum.model.Task;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private HashMap<Integer, Node> historyMap;

    public InMemoryHistoryManager() {

        this.historyMap = new HashMap<>();

    }

    public void add(Task task) {

        remove(task.getId());

        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        historyMap.put(task.getId(), newNode);
    }

    public void remove(int id) {
        Node nodeToRemove = historyMap.get(id);
        if (nodeToRemove != null) {
            if (nodeToRemove.prev != null) {
                nodeToRemove.prev.next = nodeToRemove.next;
            } else {
                head = nodeToRemove.next;
            }

            if (nodeToRemove.next != null) {
                nodeToRemove.next.prev = nodeToRemove.prev;
            } else {
                tail = nodeToRemove.prev;
            }

            historyMap.remove(id);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();

        Node current = head;

        while (current != null) {

            tasks.add(current.task);

            current = current.next;

        }

        return tasks;
    }
}
