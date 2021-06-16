package model.round;

import model.User;
import model.game.Game;

import java.util.*;

public abstract class Round {

    private final Game game;
    private final char letter;
    private RoundState state;
    private final Map<User, UserWords> usersWords;

    public Round(Game game) {
        this.game = game;
        letter = (char) new Random().ints(65, 91).findAny().getAsInt();
        state = RoundState.PLAY;
        usersWords = new HashMap<>();
        onStart();
    }

    abstract void onStart();

    public void setState(RoundState state) {
        this.state = state;
    }
}
