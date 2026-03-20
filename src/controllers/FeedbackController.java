package controllers;

import java.util.ArrayList;
import java.util.stream.Collectors;
import models.Feedback;
import services.FileHandler;

public class FeedbackController {
    private final ArrayList<Feedback> feedbackList = new ArrayList<>();
    private final String FILE_PATH = "src/database/feedback.txt";

    public FeedbackController() {
        loadFeedback();
    }

    private void loadFeedback() {
        ArrayList<String> data = FileHandler.readData(FILE_PATH);
        feedbackList.clear();
        for (String line : data) {
            String[] p = line.split("\\|");
            if (p.length == 6) {
                feedbackList.add(new Feedback(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4], p[5]));
            }
        }
    }

    public ArrayList<Feedback> getAllFeedback() {
        loadFeedback(); // Ensure we have latest
        return feedbackList;
    }

    public ArrayList<Feedback> filterByRating(int rating) {
        return feedbackList.stream()
            .filter(f -> f.getRating() == rating)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public void deleteFeedback(String id) {
        feedbackList.removeIf(f -> f.getFeedbackId().equals(id));
        saveFeedback();
    }

    private void saveFeedback() {
        ArrayList<String> data = new ArrayList<>();
        for (Feedback f : feedbackList) data.add(f.toString());
        FileHandler.writeData(FILE_PATH, data);
    }
}