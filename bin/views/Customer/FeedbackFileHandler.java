package views.Customer;

import java.io.*;
import java.util.*;

public class FeedbackFileHandler {

    // ================= READ =================
    public static List<String> readAll(String file) {

        List<String> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    list.add(line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================= WRITE ALL =================
    public static void writeAll(String file, List<String> lines) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= APPEND =================
    public static void append(String file, String data) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(data);
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SAVE =================
    public static void saveFeedback(Feedback fb) {
        append("feedback.txt", fb.toFileString());
    }

    // ================= DELETE =================
    public static void delete(String file, String feedbackId) {

        List<String> lines = readAll(file);

        lines.removeIf(line -> {
            try {
                Feedback fb = Feedback.fromFileString(line);
                return fb.getFeedbackId().equals(feedbackId);
            } catch (Exception e) {
                return false;
            }
        });

        writeAll(file, lines);
    }

    // ================= UPDATE (FINAL FIXED VERSION) =================
    public static void update(String file,
                              String feedbackId,
                              String topic,
                              int rating,
                              String comment) {

        List<String> lines = readAll(file);

        for (int i = 0; i < lines.size(); i++) {

            try {
                Feedback fb = Feedback.fromFileString(lines.get(i));

                if (fb.getFeedbackId().equals(feedbackId)) {

                    fb.setTopic(topic);
                    fb.setRating(rating);
                    fb.setComment(comment);

                    lines.set(i, fb.toFileString());
                    break;
                }

            } catch (Exception e) {
                System.out.println("Skipping bad line: " + lines.get(i));
            }
        }

        writeAll(file, lines);
    }
}