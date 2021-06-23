package model.builder;

import model.User;
import model.game.Game;
import model.game.GameSettings;
import model.game.GameState;

import java.util.ArrayList;
import java.util.List;

public class GameBuilder {

    private String gameID;
    private GameSettings settings;
    private  List<User> users = new ArrayList<>();
    private  List<User> fixUsr = new ArrayList<>();
    private GameState state = GameState.WAITING;
    private boolean couldStart;

    public GameBuilder setGameID(String gameID) {
        this.gameID = gameID;
        return this;
    }

    public void setState(String state) {
        if(state.equals(GameState.STARTED.name())){
            this.state = GameState.STARTED;
        }else if(state.equals(GameState.CHECK.name())){
            this.state = GameState.CHECK;
        }else if(state.equals(GameState.FINISHED.name())){
            this.state = GameState.FINISHED;
        }

    }

    public GameBuilder setUsers(List<User> users) {
        this.users.addAll(users);
        return this;
    }

    public GameBuilder seiFixedUser(List<User> fu){
        this.fixUsr.addAll(fu);
        return this;
    }

    public GameBuilder setSettings(GameSettings settings) {
        this.settings = settings;
        return this;
    }
    
    public GameBuilder setIsStarted(Boolean start){
        if(start) state = GameState.STARTED;
        return this;
    }

    public Game build(){
        if (gameID != null && !gameID.isEmpty() && settings != null){
            var game = new Game(gameID, settings);
            users.forEach(game::addNewUser);
            game.setState(state);
            game.setListFixedUsers(fixUsr);
            return game;
        }
        throw new IllegalArgumentException("Not enough element to build a game");
    }
}
