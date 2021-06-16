package model.builder;

import model.User;
import model.game.Game;
import model.game.GameSettings;
import model.game.GameState;

import java.util.List;

public class GameBuilder {

    private String gameID;
    private GameSettings settings;
    private  List<String> users;
    private boolean couldStart;

    public GameBuilder setGameID(String gameID) {
        this.gameID = gameID;
        return this;
    }

    public GameBuilder setUsers(List<String> users) {
        this.users = users;
        return this;
    }

    public GameBuilder setSettings(GameSettings settings) {
        this.settings = settings;
        return this;
    }
    
    public GameBuilder setIsStarted(Boolean start){
        this.couldStart = start;
        return this;
    }

    public Game build(){
        if (gameID != null && !gameID.isEmpty() && settings != null){
            var game = new Game(gameID, settings);
            if(users != null) {
                users.forEach(u -> game.addNewUser(new User(u)));
            }
            if(couldStart){
                game.setState(GameState.STARTED);
            }
            return game;
        }
        throw new IllegalArgumentException("Not enough element to build a game");
    }
}
