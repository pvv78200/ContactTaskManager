package com.contactmanager.model;

import java.time.LocalDateTime;

public class Task {

    private int taskId;
    private String title;
    private String description;
    private String taskType;
    private LocalDateTime dueDateTime;
    private String status;
    private int userId;
    private String repeatType;

    // Constructor without ID (for insert)
    public Task(String title, String description, String taskType,
                LocalDateTime dueDateTime, String status, int userId, String repeatType) {

        this.title = title;
        this.description = description;
        this.taskType = taskType;
        this.dueDateTime = dueDateTime;
        this.status = status;
        this.userId = userId;
        this.repeatType = repeatType;
    }

    // Constructor with ID (for fetch)
    public Task(int taskId, String title, String description, String taskType,
                LocalDateTime dueDateTime, String status, int userId, String repeatType) {

        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.taskType = taskType;
        this.dueDateTime = dueDateTime;
        this.status = status;
        this.userId = userId;
        this.repeatType = repeatType;
    }

    // Getters
    public int getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTaskType() {
        return taskType;
    }

    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    public String getStatus() {
        return status;
    }

    public int getUserId() {
        return userId;
    }

    public String getRepeatType() {
        return repeatType;
    }

    // Setters (for update)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public void setDueDateTime(LocalDateTime dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }
}