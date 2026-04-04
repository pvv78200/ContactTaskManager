package com.contactmanager.gui;

import com.contactmanager.model.Contact;
import com.contactmanager.service.ContactService;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter;
import java.util.List;

import com.contactmanager.model.User;

public class ContactPanel extends JPanel {

    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField searchField;

    private JButton addButton;
    private JButton deleteButton;
    private JButton exportButton;
    private JButton updateButton;

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private User loggedInUser;
    private ContactService service;

    public ContactPanel(User user) {

        this.loggedInUser = user;
        service = new ContactService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== 🔥 FORM PANEL (COMPACT) =====
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 8));

        formPanel.add(new JLabel("Name"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Phone"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Address"));
        addressField = new JTextField();
        formPanel.add(addressField);

        // ===== 🔍 SEARCH PANEL =====
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);

        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);

        // ===== 🔝 TOP PANEL =====
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        // ===== 🔥 BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        exportButton = new JButton("Export");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);

        // ===== 📊 TABLE =====
        String[] columns = {"ID", "Name", "Phone", "Email", "Address"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        table.setRowHeight(25); // 🔥 better look

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);

        // ===== ADD TO PANEL =====
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        addButton.addActionListener(e -> addContact());
        updateButton.addActionListener(e -> updateContact());
        deleteButton.addActionListener(e -> deleteContact());
        exportButton.addActionListener(e -> exportToFile());

        // ===== 🔍 LIVE SEARCH =====
        searchField.getDocument().addDocumentListener(new DocumentListener() {

            private void filter() {
                String text = searchField.getText();

                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        // ===== ROW SELECT → FILL FORM =====
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {

                int viewRow = table.getSelectedRow();
                int modelRow = table.convertRowIndexToModel(viewRow);

                nameField.setText(tableModel.getValueAt(modelRow, 1).toString());
                phoneField.setText(tableModel.getValueAt(modelRow, 2).toString());
                emailField.setText(tableModel.getValueAt(modelRow, 3).toString());
                addressField.setText(tableModel.getValueAt(modelRow, 4).toString());
            }
        });

        loadContacts();
    }

    private void addContact() {
        try {
            Contact contact = new Contact(
                    nameField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    addressField.getText()
            );

            if (service.addContact(contact, loggedInUser.getUserId())) {
                JOptionPane.showMessageDialog(this, "Contact Added!");
                clearFields();
                loadContacts();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void updateContact() {

        int viewRow = table.getSelectedRow();

        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a contact!");
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);

        try {
            Contact contact = new Contact(
                    id,
                    nameField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    addressField.getText()
            );

            if (service.updateContact(contact, loggedInUser.getUserId())) {
                JOptionPane.showMessageDialog(this, "Updated!");
                clearFields();
                loadContacts();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void deleteContact() {

        int viewRow = table.getSelectedRow();

        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a contact!");
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this contact?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            service.deleteContact(id, loggedInUser.getUserId());
            JOptionPane.showMessageDialog(this, "Deleted!");
            loadContacts();
        }
    }

    private void exportToFile() {
        try {
            List<Contact> contacts = service.getAllContacts(loggedInUser.getUserId());

            java.io.BufferedWriter writer =
                    new java.io.BufferedWriter(new java.io.FileWriter("contacts_backup.txt"));

            for (Contact c : contacts) {
                writer.write(c.getName() + " - " + c.getPhone());
                writer.newLine();
            }

            writer.close();

            JOptionPane.showMessageDialog(this, "Exported!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        table.clearSelection();
    }

    private void loadContacts() {

        SwingWorker<List<Contact>, Void> worker = new SwingWorker<>() {

            protected List<Contact> doInBackground() {
                return service.getAllContacts(loggedInUser.getUserId());
            }

            protected void done() {
                try {
                    List<Contact> contacts = get();
                    tableModel.setRowCount(0);

                    for (Contact c : contacts) {
                        tableModel.addRow(new Object[]{
                                c.getId(),
                                c.getName(),
                                c.getPhone(),
                                c.getEmail(),
                                c.getAddress()
                        });
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ContactPanel.this, e.getMessage());
                }
            }
        };

        worker.execute();
    }
}