package services;

import java.io.*;
import java.util.ArrayList;

public class FileHandler {
    private static final String DATA_DIR = "data/";

    public static ArrayList<String> readData(String fileName) {
        ArrayList<String> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return data;
    }

    public static void writeData(String fileName, String record) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + fileName, true))) {
            writer.write(record);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void updateLine(String fileName, String id, String newRecord) {
        ArrayList<String> data = readData(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + fileName))) {
            for (String line : data) {
                if (line.startsWith(id + "|")) {
                    writer.write(newRecord);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating file: " + e.getMessage());
        }
    }
}