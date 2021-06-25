package model.round;

import java.util.HashMap;
import java.util.Map;

public class UserScore {
    private final String userID;
    private final Map<String, WordScore> scores;

    public UserScore(String userID) {
        this.userID = userID;
        this.scores = new HashMap<>();
    }

    public void addWordScore(String category, WordScore ws){
        scores.put(category, ws);
    }

    public String getUserID() {
        return userID;
    }

    public Map<String, WordScore> getScores() {
        return scores;
    }

    public Integer getTotalScore() {
        return scores.values().stream().map(WordScore::getScore).reduce(0, Integer::sum);
    }

    @Override
    public String toString() {
        return "UserScore{" +
                "userID='" + userID + '\'' +
                ", scores=" + scores +
                '}';
    }
}
