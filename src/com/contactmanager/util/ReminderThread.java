package com.contactmanager.util;

import com.contactmanager.dao.TaskDAO;
import com.contactmanager.model.Task;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;

import javax.sound.sampled.*;
import java.io.InputStream;

public class ReminderThread extends Thread {

    private int userId;
    private TaskDAO dao = new TaskDAO();

    public ReminderThread(int userId) {
        this.userId = userId;
    }

    @Override
    public void run() {

        while (true) {

            try {

                List<Task> tasks = dao.getTasksByUser(userId);

                for (Task task : tasks) {

                    if (task.getStatus().equalsIgnoreCase("Pending")) {

                        if (task.getDueDateTime().isBefore(LocalDateTime.now())
                                || task.getDueDateTime().isEqual(LocalDateTime.now())) {

                            playSound();
                            // 🔔 SHOW POPUP
                            JOptionPane.showMessageDialog(null,
                                    "🔔 Reminder!\n\n" +
                                            "Task: " + task.getTitle() +
                                            "\nType: " + task.getTaskType() +
                                            "\nTime: " + task.getDueDateTime()
                            );

                            // ✅ Mark as Done (avoid repeat)
                            String repeat = task.getRepeatType();

                            if (repeat.equals("NONE")) {

                                // One-time task
                                task.setStatus("Done");

                            } else if (repeat.equals("DAILY")) {

                                // Medicine
                                task.setDueDateTime(task.getDueDateTime().plusDays(1));

                            } else if (repeat.equals("MONTHLY")) {

                                // Bill
                                task.setDueDateTime(task.getDueDateTime().plusMonths(1));

                            } else if (repeat.equals("YEARLY")) {

                                // Birthday
                                task.setDueDateTime(task.getDueDateTime().plusYears(1));
                            }

                            dao.updateTask(task);
                        }
                    }
                }

                // ⏳ Check every 10 seconds
                Thread.sleep(10000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    private void playSound() {

        try {

            InputStream audioSrc = getClass().getResourceAsStream("/resources/alert.wav");

            if (audioSrc == null) {
                System.out.println("Sound file not found!");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrc);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}