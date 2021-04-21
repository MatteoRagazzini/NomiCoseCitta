package model.builder;

import model.GameSettings;
import model.RoundType;

import java.util.List;

public class GameSettingsBuilder {
    private int numberOfRounds;
    private RoundType roundType;
    private List<String> categories;
    private int numberOfUsers;

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

    public GameSettings build(){
        return new GameSettings(numberOfRounds, roundType, categories, numberOfUsers);
    }
}
