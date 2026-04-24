package com.quiz;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class QuizLeaderboard {

    private static final String REG_NO = "RA2311047010037";
    private static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    private static final int TOTAL_POLLS = 10;
    private static final int DELAY_MS = 5000;

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        Set<String> seenEvents = new HashSet<>();
        Map<String, Integer> scores = new LinkedHashMap<>();

        System.out.println("=== Starting Quiz Leaderboard System ===\n");

        for (int poll = 0; poll < TOTAL_POLLS; poll++) {
            System.out.printf("Polling %d/9...%n", poll);
            String url = BASE_URL + "/quiz/messages?regNo=" + REG_NO + "&poll=" + poll;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("  Bad response: " + response.statusCode());
            } else {
                System.out.println("  Response: " + response.body());
                parseAndAggregate(response.body(), seenEvents, scores);
            }
            if (poll < TOTAL_POLLS - 1) {
                System.out.println("  Waiting 5 seconds...\n");
                Thread.sleep(DELAY_MS);
            }
        }

        System.out.println("\n=== Aggregated Scores ===");
        scores.forEach((p, s) -> System.out.println("  " + p + ": " + s));

        List<Map.Entry<String, Integer>> leaderboard = new ArrayList<>(scores.entrySet());
        leaderboard.sort((a, b) -> b.getValue() - a.getValue());

        int totalScore = leaderboard.stream().mapToInt(Map.Entry::getValue).sum();
        System.out.println("Total score: " + totalScore);

        StringBuilder leaderboardJson = new StringBuilder("[\n");
        for (int i = 0; i < leaderboard.size(); i++) {
            Map.Entry<String, Integer> entry = leaderboard.get(i);
            leaderboardJson.append("    {\"participant\": \"")
                    .append(entry.getKey()).append("\", \"totalScore\": ")
                    .append(entry.getValue()).append("}");
            if (i < leaderboard.size() - 1)
                leaderboardJson.append(",");
            leaderboardJson.append("\n");
        }
        leaderboardJson.append("  ]");

        String submitBody = "{\n  \"regNo\": \"" + REG_NO + "\",\n  \"leaderboard\": "
                + leaderboardJson + "\n}";

        System.out.println("\n=== Submitting Leaderboard ===");
        System.out.println(submitBody);

        HttpRequest submitRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/quiz/submit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(submitBody))
                .build();

        HttpResponse<String> submitResponse = client.send(submitRequest,
                HttpResponse.BodyHandlers.ofString());

        System.out.println("\n=== Submission Result ===");
        System.out.println(submitResponse.body());
    }

    private static void parseAndAggregate(String json, Set<String> seenEvents,
            Map<String, Integer> scores) {
        int eventsStart = json.indexOf("\"events\"");
        if (eventsStart == -1)
            return;
        int arrayStart = json.indexOf('[', eventsStart);
        int arrayEnd = json.lastIndexOf(']');
        if (arrayStart == -1 || arrayEnd == -1)
            return;
        String[] chunks = json.substring(arrayStart + 1, arrayEnd).split("\\}");
        int newCount = 0, dupCount = 0;
        for (String chunk : chunks) {
            chunk = chunk.trim();
            if (chunk.isEmpty() || chunk.equals(","))
                continue;
            String roundId = extractValue(chunk, "roundId");
            String participant = extractValue(chunk, "participant");
            String scoreStr = extractValue(chunk, "score");
            if (roundId == null || participant == null || scoreStr == null)
                continue;
            String key = roundId + "|" + participant;
            if (seenEvents.contains(key)) {
                dupCount++;
                continue;
            }
            seenEvents.add(key);
            scores.merge(participant, Integer.parseInt(scoreStr.trim()), Integer::sum);
            newCount++;
        }
        System.out.printf("  Processed: %d new, %d duplicates skipped%n", newCount, dupCount);
    }

    private static String extractValue(String json, String key) {
        int keyIdx = json.indexOf("\"" + key + "\"");
        if (keyIdx == -1)
            return null;
        int colonIdx = json.indexOf(':', keyIdx);
        if (colonIdx == -1)
            return null;
        String rest = json.substring(colonIdx + 1).trim();
        if (rest.startsWith("\"")) {
            int start = rest.indexOf('"') + 1;
            return rest.substring(start, rest.indexOf('"', start));
        } else {
            StringBuilder sb = new StringBuilder();
            for (char c : rest.toCharArray()) {
                if (Character.isDigit(c) || c == '-')
                    sb.append(c);
                else if (sb.length() > 0)
                    break;
            }
            return sb.length() > 0 ? sb.toString() : null;
        }
    }
}
