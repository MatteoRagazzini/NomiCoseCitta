package model.round.words;

import model.User;

import java.util.ArrayList;
import java.util.List;

public class RoundWords {
    private final List<User> onlineUsers;
    private final List<User> fixedUsers;
    private final List<UserWords> usersWords;

    public RoundWords(List<User> onlineUsers, List<User> fixedUsers) {
        this.onlineUsers = onlineUsers;
        this.fixedUsers = fixedUsers;
        usersWords = new ArrayList<>();
    }

    public void insertUserWords(UserWords words){
        usersWords.add(words);
    }

    public boolean allDelivered(){
        return usersWords.size()==onlineUsers.size();
    }

    public void insertEvaluation(Evaluation evaluation){
        evaluation.getVotes()
                .forEach(v -> usersWords.stream()
                        .filter(uw -> uw.getUserID().equals(v.getUserID()))
                        .findFirst()
                        .ifPresent(uw -> uw.updateWordsVotes(v.getVotes())));
    }

    public void getScores(){
        usersWords.forEach(uw -> System.out.println(uw.getVotes()));
    }

    public List<UserWords> getUsersWords(){
        return usersWords;
    }
}
