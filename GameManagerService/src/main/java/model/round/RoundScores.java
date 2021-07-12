package model.round;

import java.util.List;
import java.util.Set;

public class RoundScores {
    private final List<UserRoundScore> userRoundScores;
    private final Integer roundNumber;

    public RoundScores(List<UserRoundScore> userRoundScores, Integer roundNumber) {
        this.userRoundScores = userRoundScores;
        this.roundNumber = roundNumber;
    }

    public Set<String> getCategories(){
        if(userRoundScores.isEmpty()){
            return Set.of();
        }
        return userRoundScores.get(0).getScores().keySet();
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public List<UserRoundScore> getUserScores() {
         return userRoundScores;
    }

    @Override
    public String toString() {
        return "RoundScores{" +
                "roundNumber=" + roundNumber +
                "userScores=" + userRoundScores +
                '}';
    }
}
