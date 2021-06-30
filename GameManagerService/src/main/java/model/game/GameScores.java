package model.game;

import model.round.RoundScores;

import java.util.*;
import java.util.function.Consumer;

public class GameScores {
    private final List<RoundScores> scores;
    private final Map<String, UserGameScore> scoreMap;

    public GameScores() {
        scores = new ArrayList<>();
        scoreMap = new HashMap<>();
    }

    public void forEach(Consumer<? super RoundScores> action){
        scores.forEach(action);
    }

    public void insertRoundScore(RoundScores roundScores){
       scores.add(roundScores);
       roundScores.getUserScores().forEach(userRoundScore -> {
           if(scoreMap.containsKey(userRoundScore.getUserID())){
               scoreMap.get(userRoundScore.getUserID()).addRoundTotal(userRoundScore.getTotalScore());
           }else{
               UserGameScore us = new UserGameScore();
               us.addRoundTotal(userRoundScore.getTotalScore());
               scoreMap.put(userRoundScore.getUserID(), us);
           }
       });
    }

    public List<RoundScores> getScores() {
        return scores;
    }

    public Map<String, UserGameScore> getUsersGameScores(){
        return this.scoreMap;
    }

    public Map<String, Integer> getTotals(){
        Map<String, Integer> totals = new HashMap<>();
        scores.forEach(rs ->{
            rs.getUserScores().forEach(userScore -> {
                totals.merge(userScore.getUserID(), userScore.getTotalScore(), Integer::sum);
            });
        });
        return totals;
    }
    public String getWinner(){
        return getTotals().entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
    }

    @Override
    public String toString() {
        return "GameScores{" +
                "scores=" + scores +
                '}';
    }
}
