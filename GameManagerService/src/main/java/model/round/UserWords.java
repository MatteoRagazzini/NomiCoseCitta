package model.round;

import java.util.HashMap;
import java.util.Map;

public class UserWords {
    private final Map<String, String> words;
    private final Map<String, Integer> votes;

    public UserWords() {
        words = new HashMap<>();
        votes = new HashMap<>();
    }

    public void setUserWords(Map<String, String> w){
        words.putAll(w);
    }

    public void setWordsVotes(Map<String, Integer> v){
        votes.putAll(v);
    }


}
