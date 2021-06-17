package model.round;

import model.game.Game;

import java.util.Timer;
import java.util.TimerTask;

public class RoundTimer extends Round {

    private final static long ROUND_DURATION = 60000;

    public RoundTimer(Game game) {
        super(game);
    }

    private TimerTask onStopTask() {
        return new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timer Expired");
            }
        };
    }

    @Override
    void onStart() {
        Timer timerExecutor = new Timer("Round Timer");
        timerExecutor.schedule(onStopTask(),ROUND_DURATION);
    }

    @Override
    void onStop() {

    }
}
