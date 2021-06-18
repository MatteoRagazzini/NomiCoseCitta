package model.game;

import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Game {
    private final String id;
    private final List<User> users;
    private final GameSettings settings;
    private GameState state;
    private final GameScores scores;

    public Game(String id, GameSettings settings) {
        this.id = id;
        this.users = new ArrayList<>();
        this.settings = settings;
        this.scores = new GameScores();
        this.state = GameState.WAITING;
    }

    public String getId() {
        return id;
    }

    public List<User> getUsers() {
        return users;
    }

    public Optional<User> getUserByID(String userID){
        return users.stream().filter(u -> u.getNickname().equals(userID)).findFirst();
    }

    public boolean addNewUser(User user){
        if(users.size() < settings.getNumberOfUsers()){
            return users.add(user);
        }
        return false;
    }

    public boolean removeUser(User user){
        return users.remove(user);
    }

    public boolean removeUser(String userAddress){
        var user =  users.stream().filter(u->u.getAddress().equals(userAddress)).findFirst();
        return user.isPresent() && removeUser(user.get());
    }


    public GameSettings getSettings() {
        return settings;
    }

    public boolean gameCouldStart(){
        return users.size() == settings.getNumberOfUsers();
    }

    public boolean isFull(){
        return gameCouldStart();
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public boolean isStarted(){
        return state == GameState.STARTED;
    }

    public GameScores getScores() {
        return scores;
    }

    public void addRoundScores(Map<User, Integer> scores){
        this.scores.updateScore(scores);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", users=" + users +
                ", settings=" + settings +
                ", state=" + state +
                ", scores=" + scores +
                '}';
    }
}
