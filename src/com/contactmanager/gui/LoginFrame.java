package com.contactmanager.gui;

import javax.swing.*;
import java.awt.*;

import com.contactmanager.model.User;
import com.contactmanager.service.UserService;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton loginButton;

    private UserService userService = new UserService();

    public LoginFrame() {

        setTitle("Contact Manager - Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        initUI();

        setVisible(true);
    }

    private void initUI() {

        // ===== MAIN PANEL =====
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ===== TITLE =====
        JLabel title = new JLabel("Contact Manager", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Login to your account", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new GridLayout(2,1));
        titlePanel.add(title);
        titlePanel.add(subtitle);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // ===== FORM =====
        JPanel form = new JPanel(new GridLayout(2, 2, 10, 15));

        form.add(new JLabel("Username"));
        usernameField = new JTextField();
        form.add(usernameField);

        form.add(new JLabel("Password"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        mainPanel.add(form, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 10));

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // 🔥 STYLE BUTTONS
        loginButton.setFocusPainted(false);
        registerButton.setFocusPainted(false);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ===== ADD TO FRAME =====
        add(mainPanel);

        // ===== ACTIONS =====
        registerButton.addActionListener(e -> {
            dispose();
            new RegisterFrame();
        });

        loginButton.addActionListener(e -> authenticate());
    }

    private void authenticate() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        User user = userService.login(username, password);

        if (user != null) {

            JOptionPane.showMessageDialog(this, "Login Successful!");

            dispose();
            new MainFrame(user);

        } else {

            JOptionPane.showMessageDialog(this,
                    "Invalid Username or Password!",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}