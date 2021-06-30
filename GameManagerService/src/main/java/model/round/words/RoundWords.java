package model.round.words;

import model.User;

import java.util.ArrayList;
import java.util.List;

public class RoundWords {
    private final List<User> onlineUsers;
    private final List<User> fixedUsers;
    private final List<UserWords> usersWords;
    private final Integer roundNumber;
    private int evaluationDelivered;

    public RoundWords(List<User> onlineUsers, List<User> fixedUsers, Integer roundNumber) {
        this.onlineUsers = onlineUsers;
        this.fixedUsers = fixedUsers;
        this.roundNumber = roundNumber;
        usersWords = new ArrayList<>();
    }

    public void insertUserWords(UserWords words){
        usersWords.add(words);
    }

    public Integer getNumberOfOnlineUser(){
        return this.onlineUsers.size();
    }

    public boolean allDelivered(){
        return usersWords.size()==onlineUsers.size();
    }

    public boolean allEvaluationAreDelivered(){
        return evaluationDelivered == onlineUsers.size();
    }

    public void insertEvaluation(Evaluation evaluation){
        evaluation.getVotes()
                .forEach(v -> usersWords.stream()
                        .filter(uw -> uw.getUserID().equals(v.getUserID()))
                        .findFirst()
                        .ifPresent(uw -> uw.updateWordsVotes(v.getVotes())));
        evaluationDelivered++;
    }

    public void updateUserOnline(List<User> usersOnline){
        this.onlineUsers.clear();
        this.onlineUsers.addAll(usersOnline);
    }

    public List<UserWords> getUsersWords(){
        if(allDelivered() && usersWords.size() < fixedUsers.size())
            completeUsersWords();
        return usersWords;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    private void completeUsersWords(){
        fixedUsers.stream().filter(user -> !onlineUsers.contains(user)).forEach(u -> {
            var model = usersWords.get(0);
            var fixed = new UserWords(u.getNickname(), model.getGameID());
            model.getWords().forEach((category, w) -> fixed.insertWord(category, ""));
            usersWords.add(fixed);
        });
    }
}
