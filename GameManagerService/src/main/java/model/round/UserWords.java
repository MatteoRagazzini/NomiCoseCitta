package model.round;

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
    public void setWordsVotes(Map<String, Integer> v){
        votes.putAll(v);
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
}
