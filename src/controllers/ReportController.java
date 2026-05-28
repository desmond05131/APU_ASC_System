package controllers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import services.FileHandler;

public class ReportController {

    // Summary
    public Map<String, Double> getSummaryStats(String start, String end) {
        return getSummaryStats(start, end, "", "", "All");
    }

    public Map<String, Double> getSummaryStats(String start, String end,
                                               String svcFilter, String techFilter, String catFilter) {
        double revenue = 0;
        int total = 0, completed = 0, cancelled = 0;

        for (String line : FileHandler.readData("payments.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length < 5) continue;
            if (isWithinRange(p[4], start, end)) {
                try { revenue += Double.parseDouble(p[3]); } catch (NumberFormatException ignored) {}
            }
        }

        for (String line : FileHandler.readData("appointments.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length < 8) continue;
            if (!isWithinRange(p[3], start, end)) continue;
            if (!matches(p[1], svcFilter)) continue;
            if (!matches(p[6], techFilter)) continue;
            if (!matchesCategory(p[1], catFilter)) continue;
            total++;
            String status = p[2].toUpperCase();
            if (status.equals("COMPLETED") || status.equals("PAID")) completed++;
            else if (status.equals("CANCELLED")) cancelled++;
        }

        Map<String, Double> stats = new HashMap<>();
        stats.put("Revenue",   revenue);
        stats.put("Total",     (double) total);
        stats.put("Completed", (double) completed);
        stats.put("Cancelled", (double) cancelled);
        return stats;
    }

    // Monthly Revenue (bar chart)
    public Map<String, Double> getMonthlyRevenue() {
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        Map<String, Double> monthly = new LinkedHashMap<>();
        for (String m : months) monthly.put(m, 0.0);

        for (String line : FileHandler.readData("payments.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length < 5) continue;
            try {
                String[] dp = p[4].split("[-\\s]");
                int idx = Integer.parseInt(dp[1]) - 1;
                if (idx >= 0 && idx < 12)
                    monthly.merge(months[idx], Double.parseDouble(p[3]), Double::sum);
            } catch (Exception ignored) {}
        }

        Map<String, Double> withData = new LinkedHashMap<>();
        monthly.forEach((k, v) -> { if (v > 0) withData.put(k, v); });
        return withData.isEmpty() ? monthly : withData;
    }

    // Service Breakdown
    public List<String[]> getServiceBreakdown(String start, String end,
                                              String svcFilter, String techFilter, String catFilter) {
        Map<String, double[]> map = new LinkedHashMap<>();
        for (String line : FileHandler.readData("appointments.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length < 8) continue;
            if (!isWithinRange(p[3], start, end)) continue;
            String status = p[2].toUpperCase();
            if (!status.equals("COMPLETED") && !status.equals("PAID")) continue;
            if (!matches(p[1], svcFilter)) continue;
            if (!matches(p[6], techFilter)) continue;
            if (!matchesCategory(p[1], catFilter)) continue;
            try {
                double amt = Double.parseDouble(p[4]);
                map.computeIfAbsent(p[1], k -> new double[]{0, 0});
                map.get(p[1])[0] += amt;
                map.get(p[1])[1]++;
            } catch (NumberFormatException ignored) {}
        }
        List<String[]> result = new ArrayList<>();
        map.forEach((svc, v) -> result.add(new String[]{
            String.format("%.2f", v[0]), svc, String.valueOf((int) v[1])
        }));
        return result;
    }

    // Technician Breakdown
    public List<String[]> getTechnicianBreakdown(String start, String end,
                                                 String svcFilter, String techFilter, String catFilter) {
        Map<String, String>   names = loadTechnicianNames();
        Map<String, double[]> map   = new LinkedHashMap<>();

        for (String line : FileHandler.readData("appointments.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length < 8) continue;
            if (!isWithinRange(p[3], start, end)) continue;
            String status = p[2].toUpperCase();
            if (!status.equals("COMPLETED") && !status.equals("PAID")) continue;
            if (!matches(p[1], svcFilter)) continue;
            String techId   = p[6];
            String techName = names.getOrDefault(techId, techId);
            if (!matches(techName, techFilter)) continue;
            if (!matchesCategory(p[1], catFilter)) continue;
            try {
                double amt = Double.parseDouble(p[4]);
                map.computeIfAbsent(techId, k -> new double[]{0, 0});
                map.get(techId)[0] += amt;
                map.get(techId)[1]++;
            } catch (NumberFormatException ignored) {}
        }
        List<String[]> result = new ArrayList<>();
        map.forEach((id, v) -> result.add(new String[]{
            String.format("%.2f", v[0]), names.getOrDefault(id, id), String.valueOf((int) v[1])
        }));
        return result;
    }

    // Average Rating
    public double getAverageRating() {
        double sum = 0; int count = 0;
        for (String line : FileHandler.readData("feedback.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length < 2) continue;
            // New 9-field format: feedbackId|customerId|rating|... (p[1] non-numeric)
            // Old 8-field format: feedbackId|rating|...            (p[1] numeric)
            int ratingIdx = isNumeric(p[1]) ? 1 : 2;
            int statusIdx = isNumeric(p[1]) ? 7 : 8;
            if (p.length > statusIdx && "DELETED".equals(p[statusIdx])) continue;
            if (p.length > ratingIdx) {
                try { sum += Integer.parseInt(p[ratingIdx].trim()); count++; }
                catch (NumberFormatException ignored) {}
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    // Text Export
    public String buildReportText(String start, String end,
                                  String svcFilter, String techFilter, String catFilter) {
        Map<String, Double> stats  = getSummaryStats(start, end, svcFilter, techFilter, catFilter);
        List<String[]>      svc    = getServiceBreakdown(start, end, svcFilter, techFilter, catFilter);
        List<String[]>      tech   = getTechnicianBreakdown(start, end, svcFilter, techFilter, catFilter);
        double              avgRat = getAverageRating();

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("     APU AUTOMOTIVE SERVICE CENTRE - PERFORMANCE REPORT\n");
        sb.append("============================================================\n");
        sb.append(String.format("Period    : %s  to  %s%n",
                start.isEmpty() ? "All" : start, end.isEmpty() ? "All" : end));
        sb.append(String.format("Generated : %s%n%n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        sb.append("--- SUMMARY ---\n");
        sb.append(String.format("Total Appointments : %d%n",      stats.get("Total").intValue()));
        sb.append(String.format("Completed          : %d%n",      stats.get("Completed").intValue()));
        sb.append(String.format("Cancelled          : %d%n",      stats.get("Cancelled").intValue()));
        sb.append(String.format("Total Revenue      : RM %.2f%n", stats.get("Revenue")));
        sb.append(String.format("Average Rating     : %.1f / 5.0%n%n", avgRat));
        sb.append("--- SERVICE BREAKDOWN ---\n");
        sb.append(String.format("%-26s %10s %8s%n", "Service", "Revenue", "Count"));
        sb.append("-".repeat(47)).append("\n");
        for (String[] r : svc)
            sb.append(String.format("%-26s %10s %8s%n", r[1], "RM " + r[0], r[2]));
        sb.append("\n--- TECHNICIAN BREAKDOWN ---\n");
        sb.append(String.format("%-26s %10s %8s%n", "Technician", "Revenue", "Count"));
        sb.append("-".repeat(47)).append("\n");
        for (String[] r : tech)
            sb.append(String.format("%-26s %10s %8s%n", r[1], "RM " + r[0], r[2]));
        sb.append("\n============================================================\n");
        return sb.toString();
    }

    public void exportToFile(String reportText) throws IOException {
        String ts   = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String path = "data/report_" + ts + ".txt";
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
            w.write(reportText);
        }
    }

    // CSV Export
    public String buildReportCsv(String start, String end,
                                 String svcFilter, String techFilter, String catFilter) {
        Map<String, Double> stats  = getSummaryStats(start, end, svcFilter, techFilter, catFilter);
        List<String[]>      svc    = getServiceBreakdown(start, end, svcFilter, techFilter, catFilter);
        List<String[]>      tech   = getTechnicianBreakdown(start, end, svcFilter, techFilter, catFilter);
        double              avgRat = getAverageRating();
        String              now    = LocalDateTime.now()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        StringBuilder sb = new StringBuilder();
        sb.append("APU AUTOMOTIVE SERVICE CENTRE - PERFORMANCE REPORT\n");
        sb.append("Period,").append(start.isEmpty() ? "All" : start)
          .append(",to,").append(end.isEmpty() ? "All" : end).append("\n");
        sb.append("Generated,").append(now).append("\n\n");

        sb.append("SUMMARY\n");
        sb.append("Metric,Value\n");
        sb.append("Total Appointments,").append(stats.get("Total").intValue()).append("\n");
        sb.append("Completed,").append(stats.get("Completed").intValue()).append("\n");
        sb.append("Cancelled,").append(stats.get("Cancelled").intValue()).append("\n");
        sb.append(String.format("Total Revenue (RM),%.2f%n", stats.get("Revenue")));
        sb.append(String.format("Average Rating,%.1f%n%n", avgRat));

        sb.append("SERVICE BREAKDOWN\n");
        sb.append("Service Name,Revenue (RM),Count\n");
        for (String[] r : svc)
            sb.append(csvEscape(r[1])).append(",").append(r[0]).append(",").append(r[2]).append("\n");

        sb.append("\nTECHNICIAN BREAKDOWN\n");
        sb.append("Technician,Revenue (RM),Count\n");
        for (String[] r : tech)
            sb.append(csvEscape(r[1])).append(",").append(r[0]).append(",").append(r[2]).append("\n");

        return sb.toString();
    }

    public void exportToCsvFile(String csv) throws IOException {
        String ts   = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String path = "data/report_" + ts + ".csv";
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
            w.write(csv);
        }
    }

    // Helpers
    private boolean matches(String value, String filter) {
        return filter == null || filter.isEmpty()
                || value.toLowerCase().contains(filter.toLowerCase());
    }

    /**
     * Matches a service type name against a category filter by looking up
     * the service's category in services.txt (Normal / Major).
     * Falls back to contains-match if the service is not found.
     */
    private boolean matchesCategory(String serviceType, String cat) {
        if (cat == null || cat.isEmpty() || "All".equals(cat)) return true;
        for (String line : FileHandler.readData("services.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            // Format: id|name|category|description|price|status
            if (p.length >= 3 && p[1].equalsIgnoreCase(serviceType)) {
                return p[2].equalsIgnoreCase(cat);
            }
        }
        // Service not in catalogue - fall back to name contains-match
        return serviceType.toLowerCase().contains(cat.toLowerCase());
    }

    private boolean isWithinRange(String date, String start, String end) {
        if (start == null || end == null || start.isEmpty() || end.isEmpty()) return true;
        String d = date.length()  >= 10 ? date.substring(0, 10)  : date;
        String s = start.length() >= 10 ? start.substring(0, 10) : start;
        String e = end.length()   >= 10 ? end.substring(0, 10)   : end;
        return d.compareTo(s) >= 0 && d.compareTo(e) <= 0;
    }

    private Map<String, String> loadTechnicianNames() {
        Map<String, String> names = new HashMap<>();
        for (String line : FileHandler.readData("users.txt")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length >= 4 && "Technician".equals(p[3])) names.put(p[0], p[2]);
        }
        return names;
    }

    private static boolean isNumeric(String s) {
        try { Integer.parseInt(s.trim()); return true; }
        catch (NumberFormatException e) { return false; }
    }

    private static String csvEscape(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n"))
            return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }
}
