package model.game;

import model.User;
import model.round.RoundScores;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Game {
    private final String id;
    private final List<User> users;
    private final List<User> fixedUsers;
    private final GameSettings settings;
    private int playedRounds;
    private GameState state;
    private final GameScores scores;

    public Game(String id, GameSettings settings) {
        this.id = id;
        this.users = new ArrayList<>();
        this.fixedUsers = new ArrayList<>();
        this.settings = settings;
        this.scores = new GameScores();
        this.state = GameState.WAITING;
        this.playedRounds = 0;
    }

    public String getId() {
        return id;
    }

    public List<User> getOnlineUsers() {
        return users;
    }

    public List<User> getUsers() {
        return fixedUsers;
    }

    public boolean hasNextRound() {
        return playedRounds < settings.getNumberOfRounds();
    }

    public boolean userAlreadyPresent(User user){
        return users.stream().anyMatch(u -> u.getNickname().equals(user.getNickname()));
    }

    public boolean addNewUser(User user){
        if(users.size() < settings.getNumberOfUsers()){
            if(state != GameState.WAITING ){
                return fixedUsers.contains(user) && users.add(user);
            }
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

    public void setListFixedUsers(List<User> fu){
        fixedUsers.clear();
        fixedUsers.addAll(fu);
    }

    public GameSettings getSettings() {
        return settings;
    }

    public boolean gameCouldStart(){
        return users.size() == settings.getNumberOfUsers();
    }

    public Integer getPlayedRounds() {
        return playedRounds;
    }

    public void setPlayedRounds(Integer playedRounds){
        this.playedRounds = playedRounds;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        if(state==GameState.STARTED && fixedUsers.isEmpty()){
            fixedUsers.addAll(users);
        }
        this.state = state;
    }


    public boolean isStarted(){
        return state == GameState.STARTED;
    }

    public boolean isFinished() {
        return state == GameState.FINISHED;
    }

    public boolean roundIsStarted(){
        return state != GameState.WAITING && state != GameState.FINISHED;
    }

    public GameScores getScores() {
        return scores;
    }

    public void addRoundScores(RoundScores scores){
        this.setState(GameState.SCORE);
        this.playedRounds++;
        this.scores.insertRoundScore(scores);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", fixedUsers=" + fixedUsers +
                ", users=" + users +
                ", settings=" + settings +
                ", state=" + state +
                ", playedRounds=" + playedRounds +
                ", scores=" + scores +
                '}';
    }
}
