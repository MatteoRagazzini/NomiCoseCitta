package model.game;

import model.User;
import model.round.RoundScores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScores {
    private final List<RoundScores> scores;

    public GameScores() {
        scores = new ArrayList<>();
    }


    public void insertRoundScore(RoundScores roundScores){
       scores.add(roundScores);
    }

}
