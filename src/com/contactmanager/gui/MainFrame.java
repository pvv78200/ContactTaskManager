package com.contactmanager.gui;

import javax.swing.*;
import java.awt.*;

import com.contactmanager.model.User;
import com.contactmanager.service.ContactService;
import com.contactmanager.service.TaskService;

public class MainFrame extends JFrame {

    private JButton logoutButton;
    private User loggedInUser;

    private JLabel contactCountLabel;
    private JLabel taskCountLabel;
    private JLabel pendingCountLabel;

    public MainFrame(User user) {

        new com.contactmanager.util.ReminderThread(user.getUserId()).start();

        this.loggedInUser = user;

        setTitle("Contact & Task Manager");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== TOP BAR =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        logoutButton = new JButton("Logout");

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // ===== STATS PANEL =====
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contactCountLabel = createStatCard("Contacts");
        taskCountLabel = createStatCard("Tasks");
        pendingCountLabel = createStatCard("Pending");

        statsPanel.add(contactCountLabel.getParent());
        statsPanel.add(taskCountLabel.getParent());
        statsPanel.add(pendingCountLabel.getParent());

        // Combine top + stats
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(statsPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // ===== TABS =====
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Contacts", new ContactPanel(user));
        tabbedPane.addTab("Tasks", new TaskPanel(user));

        add(tabbedPane, BorderLayout.CENTER);

        logoutButton.addActionListener(e -> logout());

        // 🔥 LOAD STATS
        loadStats();

        startStatsAutoRefresh();

        setVisible(true);
    }

    // ===== CREATE CARD =====
    private JLabel createStatCard(String title) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        card.setBackground(new Color(45,45,45));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setForeground(Color.LIGHT_GRAY);

        JLabel valueLabel = new JLabel("0", JLabel.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return valueLabel;
    }

    private void startStatsAutoRefresh() {

        Timer timer = new Timer(2000, e -> {
            loadStats(); // refresh stats every 5 seconds
        });

        timer.start();
    }

    // ===== LOAD STATS =====
    private void loadStats() {

        ContactService contactService = new ContactService();
        TaskService taskService = new TaskService();

        int contacts = contactService.getAllContacts(loggedInUser.getUserId()).size();
        int tasks = taskService.getTasksByUser(loggedInUser.getUserId()).size();
        int pending = taskService.getPendingTasksCount(loggedInUser.getUserId());

        contactCountLabel.setText(String.valueOf(contacts));
        taskCountLabel.setText(String.valueOf(tasks));
        pendingCountLabel.setText(String.valueOf(pending));
    }

    private void logout() {

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            dispose();
            new LoginFrame();
        }
    }
}