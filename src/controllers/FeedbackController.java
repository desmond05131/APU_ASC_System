package controllers;

import java.util.ArrayList;
import java.util.stream.Collectors;
import models.Feedback;
import services.FileHandler;

public class FeedbackController {
    private final ArrayList<Feedback> feedbackList = new ArrayList<>();
    private final String FILE_PATH = "src/database/feedback.txt";

    public FeedbackController() {
        load();
    }

    private void load() {
        ArrayList<String> data = FileHandler.readData(FILE_PATH);
        feedbackList.clear();
        for (String line : data) {
            String[] p = line.split("\\|");
            if (p.length == 5) {
                // Correctly mapping indices: ID, Rating, Name, Comment, Date
                feedbackList.add(new Feedback(p[0], Integer.parseInt(p[1]), p[2], p[3], p[4]));
            }
        }
    }

    public ArrayList<Feedback> getAllFeedback() {
        load(); // Refresh from file
        return feedbackList;
    }

    public ArrayList<Feedback> search(String id, String ratingFilter) {
        load(); // Refresh before filtering
        return feedbackList.stream()
            .filter(f -> (id.isEmpty() || f.getFeedbackId().toLowerCase().contains(id.toLowerCase())))
            .filter(f -> (ratingFilter.equals("All") || (f.getRating() + " Star").equals(ratingFilter)))
            .collect(Collectors.toCollection(ArrayList::new));
    }
}