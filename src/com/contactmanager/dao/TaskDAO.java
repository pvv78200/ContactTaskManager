package com.contactmanager.dao;

import com.contactmanager.model.Task;
import com.contactmanager.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    // ✅ ADD TASK
    public boolean addTask(Task task) {

        String sql = "INSERT INTO tasks (title, description, task_type, due_datetime, status, user_id, repeat_type) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getTaskType());
            ps.setTimestamp(4, Timestamp.valueOf(task.getDueDateTime()));
            ps.setString(5, task.getStatus());
            ps.setInt(6, task.getUserId());
            ps.setString(7, task.getRepeatType());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ✅ GET TASKS BY USER
    public List<Task> getTasksByUser(int userId) {

        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                tasks.add(new Task(
                        rs.getInt("task_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("task_type"),
                        rs.getTimestamp("due_datetime").toLocalDateTime(),
                        rs.getString("status"),
                        rs.getInt("user_id"),
                        rs.getString("repeat_type")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    // ✅ UPDATE TASK
    public boolean updateTask(Task task) {

        String sql = "UPDATE tasks SET title=?, description=?, task_type=?, due_datetime=?, status=? WHERE task_id=? AND user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getTaskType());
            ps.setTimestamp(4, Timestamp.valueOf(task.getDueDateTime()));
            ps.setString(5, task.getStatus());
            ps.setInt(6, task.getTaskId());
            ps.setInt(7, task.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ✅ DELETE TASK
    public boolean deleteTask(int taskId, int userId) {

        String sql = "DELETE FROM tasks WHERE task_id=? AND user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taskId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}