package model.builder;

import model.Game;
import model.GameSettings;

public class GameBuilder {

    private String gameID;
    private GameSettings settings;

    public GameBuilder setGameID(String gameID) {
        this.gameID = gameID;
        return this;
    }

    public GameBuilder setSettings(GameSettings settings) {
        this.settings = settings;
        return this;
    }

    public Game build(){
        if (gameID != null && !gameID.isEmpty() && settings != null){
            return new Game(gameID, settings);
        }
        throw new IllegalArgumentException("Not enough element to build a game");
    }
}
