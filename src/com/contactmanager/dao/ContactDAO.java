package com.contactmanager.dao;

import com.contactmanager.model.Contact;
import com.contactmanager.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO {

    public boolean addContact(Contact contact, int userId) {

        String sql = "INSERT INTO contacts(name, phone, email, address, user_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, contact.getName());
            ps.setString(2, contact.getPhone());
            ps.setString(3, contact.getEmail());
            ps.setString(4, contact.getAddress());
            ps.setInt(5, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Contact> getAllContacts(int userId) {

        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                contacts.add(new Contact(
                        rs.getInt("contact_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contacts;
    }

    public boolean updateContact(Contact contact, int userId) {

        String sql = "UPDATE contacts SET name=?, phone=?, email=?, address=? WHERE contact_id=? AND user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, contact.getName());
            ps.setString(2, contact.getPhone());
            ps.setString(3, contact.getEmail());
            ps.setString(4, contact.getAddress());
            ps.setInt(5, contact.getId());
            ps.setInt(6, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteContact(int id, int userId) {

        String sql = "DELETE FROM contacts WHERE contact_id=? AND user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean contactExists(String phone, int userId) {

        String sql = "SELECT 1 FROM contacts WHERE phone = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();
            return rs.next(); // true if exists

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}