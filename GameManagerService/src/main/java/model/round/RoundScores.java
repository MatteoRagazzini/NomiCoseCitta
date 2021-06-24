package model.round;

import model.User;

import java.util.ArrayList;
import java.util.List;

public class RoundScores {
    private final List<UserScore> userScores;

    public RoundScores(List<UserScore> userScores) {
        this.userScores = userScores;
    }

    @Override
    public String toString() {
        return "RoundScores{" +
                "userScores=" + userScores +
                '}';
    }
}
