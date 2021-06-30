package model.game;



import model.round.RoundType;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameSettings {
    private final int numberOfRounds;
    private final RoundType roundType;
    private final List<String> categories;
    private final List<Integer> roundsLetter;
    private final int numberOfUsers;

    public GameSettings(int numberOfRounds, RoundType roundType, List<String> categories, int numberOfUsers) {
        this.numberOfRounds = numberOfRounds;
        this.roundType = roundType;
        this.categories = categories;
        this.numberOfUsers = numberOfUsers;
        this.roundsLetter = new Random().ints(numberOfRounds,65, 91).boxed().collect(Collectors.toList());
    }

    public char getRoundLetter(int round){
        return (char) roundsLetter.get(round).intValue();
    }

    public List<Character> getRoundsLetter() {
        return roundsLetter.stream().map(i -> (char)i.intValue()).collect(Collectors.toList());
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public RoundType getRoundType() {
        return roundType;
    }

    public List<String> getCategories() {
        return categories;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    @Override
    public String toString() {
        return "GameSettings{" +
                "numberOfRounds=" + numberOfRounds +
                ", roundType=" + roundType +
                ", categories=" + categories +
                ", numberOfUsers=" + numberOfUsers +
                '}';
    }
}
