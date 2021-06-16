package model.game;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class GameScores {
    private final Map<User, Integer> scores;

    public GameScores() {
        scores = new HashMap<>();
    }

    public Map<User, Integer> getScores() {
        return scores;
    }

    public void updateScore(Map<User,Integer> roundScores){
        throw new UnsupportedOperationException("To do");
    }

}
