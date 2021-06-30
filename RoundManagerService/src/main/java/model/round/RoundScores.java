package model.round;

import java.util.List;
import java.util.Set;

public class RoundScores {
    private final List<UserScore> userScores;

    public RoundScores(List<UserScore> userScores) {
        this.userScores = userScores;
    }

    public Set<String> getCategories(){
        if(userScores.isEmpty()){
            return Set.of();
        }
        return userScores.get(0).getScores().keySet();
    }

    public List<UserScore> getUserScores() {
        return userScores;
    }

    @Override
    public String toString() {
        return "RoundScores{" +
                "userScores=" + userScores +
                '}';
    }
}
