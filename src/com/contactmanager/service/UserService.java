package com.contactmanager.service;

import com.contactmanager.model.User;
import com.contactmanager.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserService {

    public User login(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    public boolean register(User user) {

        String checkSql = "SELECT * FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            // Check if username already exists
            checkStmt.setString(1, user.getUsername());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return false; // username exists
            }

            // Insert new user
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                insertStmt.setString(1, user.getUsername());
                insertStmt.setString(2, user.getPassword());

                return insertStmt.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}




