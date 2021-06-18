package model.round;

import java.util.ArrayList;
import java.util.List;

public class RoundWords {
    private final Integer userNumber;
    private final List<UserWords> usersWords;

    public RoundWords(Integer userNumber) {
        this.userNumber = userNumber;
        usersWords = new ArrayList<>();
    }

    public Integer getUserNumber() {
        return userNumber;
    }

    public void insertUserWords(UserWords words){
        usersWords.add(words);
    }

    public boolean allDelivered(){
        return usersWords.size()==userNumber;
    }

    public List<UserWords> getUsersWords(){
        return usersWords;
    }
}
