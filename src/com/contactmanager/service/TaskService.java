package com.contactmanager.service;

import com.contactmanager.dao.TaskDAO;
import com.contactmanager.model.Task;
import com.contactmanager.util.InvalidDataException;

import java.time.LocalDateTime;
import java.util.List;

public class TaskService {

    private TaskDAO dao = new TaskDAO();

    // ✅ ADD TASK WITH VALIDATION
    public boolean addTask(Task task) throws InvalidDataException {

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new InvalidDataException("Title cannot be empty!");
        }

        if (task.getTaskType() == null || task.getTaskType().trim().isEmpty()) {
            throw new InvalidDataException("Task type must be selected!");
        }

        if (task.getDueDateTime() == null) {
            throw new InvalidDataException("Date & Time cannot be empty!");
        }

        if (task.getDueDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidDataException("Due time must be in the future!");
        }

        return dao.addTask(task);
    }

    // ✅ GET TASKS BY USER
    public List<Task> getTasksByUser(int userId) {
        return dao.getTasksByUser(userId);
    }

    // ✅ UPDATE TASK
    public boolean updateTask(Task task) throws InvalidDataException {

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new InvalidDataException("Title cannot be empty!");
        }

        if (task.getDueDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidDataException("Due time must be in the future!");
        }

        return dao.updateTask(task);
    }

    // ✅ DELETE TASK
    public boolean deleteTask(int taskId, int userId) {
        return dao.deleteTask(taskId, userId);
    }


    public int getPendingTasksCount(int userId) {

        int count = 0;

        try {
            String sql = "SELECT COUNT(*) FROM tasks WHERE user_id=? AND status='Pending'";
            java.sql.Connection con = com.contactmanager.util.DBConnection.getConnection();
            java.sql.PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, userId);

            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
}