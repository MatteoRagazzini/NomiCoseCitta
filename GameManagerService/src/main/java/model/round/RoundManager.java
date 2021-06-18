package model.round;

import com.rabbitmq.client.DeliverCallback;
import model.game.Game;
import presentation.Presentation;
import rabbit.Consumer;
import rabbit.MessageType;
import rabbit.RPCServer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RoundManager {

    private final Map<String, Round> activeRounds;

    public RoundManager() {
        activeRounds = new HashMap<>();
        new Consumer("game", startGame() , MessageType.START);
        new RPCServer(getCallbackMap());

    }

    private Map<MessageType, Function<String, String>> getCallbackMap() {
        Map<MessageType, Function<String,String>> map = new HashMap<>();
        map.put(MessageType.WORDS, sendWordsToRound());
        return map;
    }

    private Function<String, String> sendWordsToRound() {
        return  msg -> {
            try {
                var userWords = Presentation.deserializeAs(msg, UserWords.class);
                var round = activeRounds.get(userWords.getGameID());
                round.insertUserWord(userWords);
                if(round.getUsersWords().allDelivered()){
                    return Presentation.serializerOf(RoundWords.class).serialize(round.getUsersWords());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };
    }

    private DeliverCallback startGame() {
        return (consumerTag, delivery) -> {
            try {
                var game = Presentation.deserializeAs(new String(delivery.getBody(),
                        "UTF-8"), Game.class);
                activeRounds.put(game.getId(), createRound(game));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private Round createRound(Game game){
        switch (game.getSettings().getRoundType()){
            case STOP:
                return new RoundStop(game);
            case TIMER:
                return new RoundTimer(game);
        }
        throw new RuntimeException("Impossible create a round because RoundType is not defined");
    }
}