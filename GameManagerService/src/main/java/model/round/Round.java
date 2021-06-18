package model.round;

import model.User;
import model.game.Game;

import java.util.*;

public abstract class Round {

    private final Game game;
    private final char letter;
    private RoundState state;
   private final RoundWords usersWords;

    public Round(Game game) {
        this.game = game;
        letter = (char) new Random().ints(65, 91).findAny().getAsInt();
        state = RoundState.PLAY;
        usersWords = new RoundWords(game.getSettings().getNumberOfUsers());
        onStart();
    }

    public Game getGame() {
        return game;
    }

    public RoundState getState() {
        return state;
    }

    public RoundWords getUsersWords() {
        return usersWords;
    }

    public void insertUserWord(UserWords userWords){
        usersWords.insertUserWords(userWords);
    }

    abstract void onStart();
    abstract void onStop();

    public void setState(RoundState state) {
        this.state = state;
    }
}
