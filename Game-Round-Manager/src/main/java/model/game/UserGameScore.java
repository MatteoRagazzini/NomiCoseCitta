package model.game;

import java.util.ArrayList;
import java.util.List;

public class UserGameScore {

    private final List<Integer> roundsTotalScores;

    public UserGameScore(){
        this(new ArrayList<>());
    }
    public UserGameScore(List<Integer> roundsTotalScores) {
        this.roundsTotalScores = roundsTotalScores;
    }

    public void addRoundTotal(Integer total){
        roundsTotalScores.add(total);
    }

    public List<Integer> getRoundsTotalScores() {
        return roundsTotalScores;
    }

    public Integer getTotal(){
        return roundsTotalScores.stream().reduce(0, Integer::sum);
    }
}
