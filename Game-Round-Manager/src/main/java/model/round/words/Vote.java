package model.round.words;

import java.util.HashMap;
import java.util.Map;

public class Vote {
    private final String userID;
    private final Map<String, Integer> map;

    public Vote(String userID) {
        this.userID = userID;
        this.map = new HashMap<>();
    }

    public void insertVote(String category, String vote){
        if(vote.equals("ok")){
            this.map.put(category, 1);
        }else {
            this.map.put(category,0);
        }
    }

    public String getUserID() {
        return userID;
    }

    public Map<String, Integer> getVotes() {
        return map;
    }
}
