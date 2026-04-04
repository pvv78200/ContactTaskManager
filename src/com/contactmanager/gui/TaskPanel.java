package com.contactmanager.gui;

import com.contactmanager.model.Task;
import com.contactmanager.model.User;
import com.contactmanager.service.TaskService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.RowFilter;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Date;
import javax.swing.SpinnerDateModel;

public class TaskPanel extends JPanel {

    private JTextField titleField;
    private JTextArea descArea;
    private JComboBox<String> typeBox;
    private JSpinner dateTimeSpinner;
    private JComboBox<String> repeatBox;
    private JTextField searchField;

    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private TaskService service;
    private User loggedInUser;

    public TaskPanel(User user) {

        this.loggedInUser = user;
        service = new TaskService();

        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // ===== 🔥 COMPACT FORM =====
        JPanel form = new JPanel(new GridLayout(2, 4, 10, 8));

        form.add(new JLabel("Title"));
        titleField = new JTextField();
        form.add(titleField);

        form.add(new JLabel("Type"));
        typeBox = new JComboBox<>(new String[]{
                "Birthday","Medicine","Alarm","Bill","Custom"
        });
        form.add(typeBox);

        form.add(new JLabel("Date & Time"));
        SpinnerDateModel model = new SpinnerDateModel();
        dateTimeSpinner = new JSpinner(model);
        dateTimeSpinner.setEditor(new JSpinner.DateEditor(dateTimeSpinner, "yyyy-MM-dd HH:mm"));
        form.add(dateTimeSpinner);

        form.add(new JLabel("Repeat"));
        repeatBox = new JComboBox<>(new String[]{
                "NONE", "DAILY", "MONTHLY", "YEARLY"
        });
        form.add(repeatBox);

        // ===== DESCRIPTION (SEPARATE FOR CLEAN LOOK) =====
        JPanel descPanel = new JPanel(new BorderLayout(5,5));
        descPanel.add(new JLabel("Description"), BorderLayout.NORTH);

        descArea = new JTextArea(2,20);
        descPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);

        // ===== SEARCH =====
        JPanel searchPanel = new JPanel(new BorderLayout(5,5));
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);

        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new BorderLayout(5,5));
        topPanel.add(form, BorderLayout.NORTH);
        topPanel.add(descPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        // ===== BUTTONS =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        btnPanel.add(addButton);
        btnPanel.add(updateButton);
        btnPanel.add(deleteButton);

        // ===== TABLE =====
        String[] cols = {"ID","Title","Type","DateTime","Status"};
        tableModel = new DefaultTableModel(cols,0);
        table = new JTable(tableModel);

        table.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(table);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // ===== ADD TO PANEL =====
        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        addButton.addActionListener(e -> addTask());
        updateButton.addActionListener(e -> updateTask());
        deleteButton.addActionListener(e -> deleteTask());

        // ===== SEARCH FILTER =====
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String text = searchField.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        loadTasks();

        startAutoRefresh();
    }

    private void startAutoRefresh() {

        Timer timer = new Timer(2000, e -> {

            // Only refresh if no row is selected
            if (table.getSelectedRow() == -1) {
                loadTasks();
            }

        });

        timer.start();
    }

    private void addTask() {
        try {
            Date date = (Date) dateTimeSpinner.getValue();
            LocalDateTime dateTime = date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();

            String type = typeBox.getSelectedItem().toString();
            String repeat;

            if (type.equals("Birthday")) repeat = "YEARLY";
            else if (type.equals("Medicine")) repeat = "DAILY";
            else if (type.equals("Bill")) repeat = "MONTHLY";
            else repeat = repeatBox.getSelectedItem().toString();

            Task task = new Task(
                    titleField.getText(),
                    descArea.getText(),
                    type,
                    dateTime,
                    "Pending",
                    loggedInUser.getUserId(),
                    repeat
            );

            if(service.addTask(task)) {
                JOptionPane.showMessageDialog(this,"Task Added!");
                clear();
                loadTasks();
            }

        } catch(Exception e){
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());
        }
    }

    private void updateTask() {

        int viewRow = table.getSelectedRow();

        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to update!");
            return;
        }

        int row = table.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(row, 0);

        try {
            Date date = (Date) dateTimeSpinner.getValue();
            LocalDateTime dateTime = date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();

            String type = typeBox.getSelectedItem().toString();
            String repeat;

            if (type.equals("Birthday")) repeat = "YEARLY";
            else if (type.equals("Medicine")) repeat = "DAILY";
            else if (type.equals("Bill")) repeat = "MONTHLY";
            else repeat = repeatBox.getSelectedItem().toString();

            Task task = new Task(
                    id,
                    titleField.getText(),
                    descArea.getText(),
                    type,
                    dateTime,
                    "Pending",
                    loggedInUser.getUserId(),
                    repeat
            );

            boolean updated = service.updateTask(task);

            if (updated) {
                JOptionPane.showMessageDialog(this, "Task Updated Successfully!");
                clear();
                loadTasks();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update task!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void deleteTask() {

        int viewRow = table.getSelectedRow();

        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete!");
            return;
        }

        int row = table.convertRowIndexToModel(viewRow);

        int id = (int) tableModel.getValueAt(row, 0);
        String title = tableModel.getValueAt(row, 1).toString();

        // ✅ CONFIRMATION
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete task:\n\n" + title + " ?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {

            boolean deleted = service.deleteTask(id, loggedInUser.getUserId());

            if (deleted) {
                JOptionPane.showMessageDialog(this, "Task Deleted Successfully!");
                loadTasks();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete task!");
            }
        }
    }

    private void loadTasks() {

        tableModel.setRowCount(0);

        List<Task> tasks = service.getTasksByUser(loggedInUser.getUserId());

        for(Task t : tasks){
            tableModel.addRow(new Object[]{
                    t.getTaskId(),
                    t.getTitle(),
                    t.getTaskType(),
                    t.getDueDateTime(),
                    t.getStatus()
            });
        }
    }

    private void fillForm() {

        int viewRow = table.getSelectedRow();
        if (viewRow == -1) return;

        int row = table.convertRowIndexToModel(viewRow);

        titleField.setText(tableModel.getValueAt(row,1).toString());
        typeBox.setSelectedItem(tableModel.getValueAt(row,2).toString());

        LocalDateTime ldt = (LocalDateTime) tableModel.getValueAt(row, 3);
        Date date = Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
        dateTimeSpinner.setValue(date);
    }

    private void clear() {
        titleField.setText("");
        descArea.setText("");
        dateTimeSpinner.setValue(new Date());
    }
}