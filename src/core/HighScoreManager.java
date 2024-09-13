package core;

import java.io.*;

public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "highscore.txt";
    private static final int NO_HIGH_SCORE = Integer.MAX_VALUE;

    public HighScoreManager() {
        initialize();
    }

    private void initialize() {
        File file = new File(HIGH_SCORE_FILE);
        if (!file.exists()) {
            saveHighScore(NO_HIGH_SCORE); // Set initial high score to a special value indicating no high score
        }
    }

    public int getHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
            return NO_HIGH_SCORE;
        }
    }

    public void saveHighScore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isNewHighScore(int newScore) {
        int currentHighScore = getHighScore();
        return currentHighScore == NO_HIGH_SCORE || newScore < currentHighScore;
    }

    public boolean hasHighScore() {
        return getHighScore() != NO_HIGH_SCORE;
    }
}
