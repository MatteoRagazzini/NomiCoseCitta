package model.round.words;

import java.util.HashMap;
import java.util.Map;

public class UserWords {
    private final String userID;
    private final String gameID;
    private final Map<String, String> words;
    private final Map<String, Integer> votes;

    public UserWords(String userID, String gameID) {
        this.userID = userID;
        this.gameID = gameID;
        words = new HashMap<>();
        votes = new HashMap<>();
    }

    public void insertWord(String category, String word){
        words.put(category,word);
    }

    public void updateWordsVotes(Map<String, Integer> v){
        v.forEach((cat, vote) -> votes.merge(cat, vote, Integer::sum));
    }

    public String getUserID() {
        return userID;
    }

    public String getGameID() {
        return gameID;
    }

    public Map<String, String> getWords() {
        return words;
    }

    public Map<String, Integer> getVotes() {
        return votes;
    }

    @Override
    public String toString() {
        return "UserWords{" +
                "userID='" + userID + '\'' +
                ", words=" + words +
                ", votes=" + votes +
                '}';
    }
}
