package model.round;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.rabbitmq.client.DeliverCallback;
import model.game.Game;
import model.game.GameState;
import model.round.words.Evaluation;
import model.round.words.RoundWords;
import model.round.words.UserWords;
import org.bson.Document;
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
    private final MongoDatabase db;
    private final MongoCollection<Document> roundsCollection;

    public RoundManager() {
        activeRounds = new HashMap<>();
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        db = client.getDatabase("NCCRounds");
        roundsCollection = db.getCollection("Rounds");
        roundsCollection.find().forEach(doc -> {
            try {
                var round = Presentation.deserializeAs(doc.toJson(), Round.class);
                activeRounds.put(round.getGame().getId(), round);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        emitter = new Emitter("round");
        new Consumer("game", getConsumerMap());
        new RPCServer(getCallbackMap());

    }

    private Map<MessageType, DeliverCallback> getConsumerMap(){
        Map<MessageType, DeliverCallback> map = new HashMap<>();
        map.put( MessageType.START, startGameRound());
        map.put( MessageType.FINISH, gameFinished());
        map.put( MessageType.DISCONNECT, userDisconnection());
        return map;
    }


    private Map<MessageType, Function<String, String>> getCallbackMap() {
        Map<MessageType, Function<String,String>> map = new HashMap<>();
        map.put(MessageType.WORDS, updateRoundWords());
        map.put(MessageType.VOTES, onVotesDelivery());
        map.put(MessageType.CHECK, sendRoundWords());
        map.put(MessageType.SCORES, sendRoundScores());
        return map;
    }

    private Function<String, String> sendRoundScores() {
        return msg -> {
            var gameID = new Gson().fromJson(msg, JsonObject.class).get("gameID").getAsString();
            return Presentation.serializerOf(RoundScores.class).serialize(activeRounds.get(gameID).getRoundScores());
        };
    }

    private Function<String, String> sendRoundWords() {
        return msg -> {
            var gameID = new Gson().fromJson(msg, JsonObject.class).get("gameID").getAsString();
            return Presentation.serializerOf(RoundWords.class).serialize(activeRounds.get(gameID).getRoundWords());
        };
    }

    private Function<String, String> onVotesDelivery() {
        return msg -> {
            try {
                var evaluation = Presentation.deserializeAs(msg, Evaluation.class);
                var round = activeRounds.get(evaluation.getGameID());
                round.insertEvaluation(evaluation);
                updateDb(round.getGame().getId(), round);
                if(round.scoresAvailable()){
                    round.getGame().addRoundScores(round.getRoundScores());
                    emitter.emit(MessageType.WORDS, Presentation.serializerOf(Game.class).serialize(round.getGame()));
                    return Presentation.serializerOf(RoundScores.class).serialize(round.getRoundScores());
                }
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
                updateDb(round.getGame().getId(), round);
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

    private DeliverCallback startGameRound() {
        return (consumerTag, delivery) -> {
            try {
                var game = Presentation.deserializeAs(new String(delivery.getBody(),
                        "UTF-8"), Game.class);
                activeRounds.put(game.getId(), createRound(game));
                if(roundsCollection.countDocuments(Filters.eq("gameID", game.getId())) == 0){
                    roundsCollection.insertOne(Document.parse(Presentation.serializerOf(Round.class)
                            .serialize(activeRounds.get(game.getId()))));
                } else {
                    updateDb(game.getId(), activeRounds.get(game.getId()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private DeliverCallback gameFinished() {
        return (consumerTag, delivery) -> {
            try {
                var gameID = new String(delivery.getBody(), "UTF-8");
                activeRounds.remove(gameID);
                removeFromDb(gameID);
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
                if(activeRounds.containsKey(game.getId())) {
                    activeRounds.get(game.getId()).updateGame(game);
                    updateDb(game.getId(), activeRounds.get(game.getId()));
                }
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

    private void updateDb(String gameID, Round round){
        roundsCollection.replaceOne(Filters.eq("gameID", gameID),convertRoundToDocument(round));
    }

    private void removeFromDb(String gameID){
        roundsCollection.deleteOne(Filters.eq("gameID", gameID));
    }

    private Document convertRoundToDocument(Round round){
        return Document.parse(Presentation.serializerOf(Round.class).serialize(round));
    }
}
