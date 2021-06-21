package model.round;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.game.Game;
import rabbit.Emitter;
import rabbit.MessageType;

import java.util.Timer;
import java.util.TimerTask;

public class RoundTimer extends Round {

    private final static long ROUND_DURATION = 60000;
    private final Emitter stopEmitter;
    private final JsonObject stopMessage;

    public RoundTimer(Game game) {
        super(game);
        stopEmitter = new Emitter("stopRound");
        stopMessage = new JsonObject();
        stopMessage.addProperty("gameID", game.getId());
    }

    private TimerTask onStopTask() {
        return new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timer Expired");
                stopEmitter.emit(MessageType.STOP, new Gson().toJson(stopMessage));
            }
        };
    }

    @Override
    void onStart() {
        Timer timerExecutor = new Timer("Round Timer");
        timerExecutor.schedule(onStopTask(),ROUND_DURATION);
        System.out.println("STARTED round timer");
    }

    @Override
    void onStop() {

    }
}
