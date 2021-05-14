package model;



import java.util.List;

public class GameSettings {
    private final int numberOfRounds;
    private final RoundType roundType;
    private final List<String> categories;
    private final int numberOfUsers;

    public GameSettings(int numberOfRounds, RoundType roundType, List<String> categories, int numberOfUsers) {
        this.numberOfRounds = numberOfRounds;
        this.roundType = roundType;
        this.categories = categories;
        this.numberOfUsers = numberOfUsers;
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
