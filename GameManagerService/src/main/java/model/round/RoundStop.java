package model.round;

import model.game.Game;

public class RoundStop extends Round{

    public RoundStop(Game game) {
        super(game);
    }

    @Override
    void onStart() {
        System.out.println("Round stop started");
    }
}
