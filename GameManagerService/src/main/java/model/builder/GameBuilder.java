package model.builder;

import model.Game;
import model.GameSettings;
import model.User;

public class GameBuilder {

    private String gameID;
    private User creator;
    private GameSettings settings;

    public GameBuilder setGameID(String gameID) {
        this.gameID = gameID;
        return this;
    }

    public GameBuilder setCreator(User creator) {
        this.creator = creator;
        return this;
    }

    public GameBuilder setSettings(GameSettings settings) {
        this.settings = settings;
        return this;
    }

    public Game build(){
        if (gameID != null && !gameID.isEmpty() && creator != null && settings != null){
            return new Game(gameID, creator, settings);
        }
        throw new IllegalArgumentException("Not enough element to build a game");
    }
}
