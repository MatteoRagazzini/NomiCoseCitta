package model.round;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rabbitmq.client.DeliverCallback;
import model.game.Game;
import model.game.GameState;
import model.round.words.Evaluation;
import model.round.words.RoundWords;
import model.round.words.UserWords;
import presentation.Presentation;
import rabbit.Consumer;
import rabbit.Emitter;
import rabbit.MessageType;
import rabbit.RPCServer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RoundManager {

    private final Map<String, Round> activeRounds;
    private final Emitter emitter;

    public RoundManager() {
        activeRounds = new HashMap<>();
        emitter = new Emitter("round");
        new Consumer("game", getConsumerMap());
        new RPCServer(getCallbackMap());

    }

    private Map<MessageType, DeliverCallback> getConsumerMap(){
        Map<MessageType, DeliverCallback> map = new HashMap<>();
        map.put( MessageType.START, startGame());
        map.put( MessageType.DISCONNECT, userDisconnection());
        return map;
    }

    private Map<MessageType, Function<String, String>> getCallbackMap() {
        Map<MessageType, Function<String,String>> map = new HashMap<>();
        map.put(MessageType.WORDS, updateRoundWords());
        map.put(MessageType.VOTES, onVotesDelivery());
        map.put(MessageType.CHECK, sendRoundWords());
        return map;
    }

    private Function<String, String> sendRoundWords() {
        return msg -> {
            System.out.println("RICEVUTA RICHIESTA PAROLE");
            var gameID = new Gson().fromJson(msg, JsonObject.class).get("gameID").getAsString();
            return Presentation.serializerOf(RoundWords.class).serialize(activeRounds.get(gameID).getRoundWords());
        };
    }

    private Function<String, String> onVotesDelivery() {
        return msg -> {
            try {
                System.out.println("RICEVUTA VALUTAZIONE");
                var evaluation = Presentation.deserializeAs(msg, Evaluation.class);
                var round = activeRounds.get(evaluation.getGameID());
                round.insertEvaluation(evaluation);
                if(round.scoresAvailable()){
                    System.out.println(round.getRoundScores());
                }
                return "Scores";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };

    }

    private Function<String, String> updateRoundWords() {
        return  msg -> {
            try {
                var userWords = Presentation.deserializeAs(msg, UserWords.class);
                var round = activeRounds.get(userWords.getGameID());
                round.insertUserWord(userWords);
                if(round.getRoundWords().allDelivered()){
                    round.getGame().setState(GameState.CHECK);
                    emitter.emit(MessageType.WORDS, Presentation.serializerOf(Game.class).serialize(round.getGame()));
                    return Presentation.serializerOf(RoundWords.class).serialize(round.getRoundWords());
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
                System.out.println("GAME INIZIATO: "+ game);
                activeRounds.put(game.getId(), createRound(game));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private DeliverCallback userDisconnection() {
        return (consumerTag, delivery) -> {
            try {
                var game = Presentation.deserializeAs(new String(delivery.getBody(),
                        "UTF-8"), Game.class);
                System.out.println("USER DISCONNESSO");
                activeRounds.get(game.getId()).updateGame(game);
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
