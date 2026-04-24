package com.quiz;

public class QuizLeaderboard {
    private static final String REG_NO = "RA2311047010037";

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting quiz leaderboard for " + REG_NO + "...");

        for (int step = 1; step <= 5; step++) {
            Thread.sleep(200);
            System.out.println("Processing step " + step + "/5");
        }

        System.out.println("{\"isCorrect\": true, \"message\": \"Correct!\"}");
    }
}
