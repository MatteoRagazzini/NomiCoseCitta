package model.builder;

import model.game.GameSettings;
import model.round.RoundType;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class GameSettingsBuilder {
    private int numberOfRounds;
    private RoundType roundType;
    private List<String> categories;
    private int numberOfUsers;
    private List<Integer> roundsLetters;

    public GameSettingsBuilder setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
        return this;
    }

    public GameSettingsBuilder setRoundType(RoundType roundType) {
        this.roundType = roundType;
        return this;
    }

    public GameSettingsBuilder setCategories(List<String> categories) {
        this.categories = categories;
        return this;
    }

    public GameSettingsBuilder setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
        return this;
    }

    public void setRoundsLetters(List<String> roundsLetters) {
        this.roundsLetters = roundsLetters.stream().map(l-> (int)l.getBytes(StandardCharsets.US_ASCII)[0]).collect(Collectors.toList());
    }

    public GameSettings build(){
        if(roundsLetters != null && !roundsLetters.isEmpty()) {
            return new GameSettings(numberOfRounds, roundType, categories, roundsLetters, numberOfUsers);
        } else {
            return new GameSettings(numberOfRounds, roundType, categories, numberOfUsers);
        }
    }
}
