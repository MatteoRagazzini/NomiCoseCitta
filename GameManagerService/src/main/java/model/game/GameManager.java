package model.game;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.rabbitmq.client.DeliverCallback;
import model.request.DisconnectRequest;
import model.request.StartRequest;
import model.request.UserInLobbyRequest;
import org.bson.Document;
import presentation.Presentation;

import rabbit.Consumer;
import rabbit.Emitter;
import rabbit.MessageType;
import rabbit.RPCServer;

import javax.swing.*;
import java.util.*;
import java.util.function.Function;

public class GameManager {

    private final List<Game> games;
    private final Emitter emitter;
    private final MongoDatabase db;
    private final MongoCollection<Document> gamesCollection;

    public GameManager() {
        games = new ArrayList<>();
        emitter = new Emitter("game");
        MongoClient client = MongoClients.create(System.getenv("MONGODB"));
        db = client.getDatabase("NCCGames");
        gamesCollection = db.getCollection("Games");
        gamesCollection.find().forEach(doc -> {
            try {
                games.add(Presentation.deserializeAs(doc.toJson(), Game.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        GameIDSupplier.getInstance().setGameCreated(games.stream()
                .map(g -> Integer.parseInt(g.getId()))
                .max(Comparator.comparingInt(i -> i))
                .orElse(0));

        new Consumer("round",getGameUpdateCallback());
        new RPCServer(getCallbackMap());
    }

    private Map<MessageType, DeliverCallback> getGameUpdateCallback() {
        Map<MessageType, DeliverCallback> map = new HashMap<>();
        map.put(MessageType.WORDS, onWordsDelivery());
        return map;
    }

    private DeliverCallback onWordsDelivery() {
        return (consumerTag, message) -> {
            try {
                Game gameUpdated = Presentation.deserializeAs(new String(message.getBody(),
                        "UTF-8"), Game.class);
                updateDb(gameUpdated);
                games.removeIf(g -> g.getId().equals(gameUpdated.getId()));
                games.add(gameUpdated);

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private Map<MessageType, Function<String,String>> getCallbackMap(){
        Map<MessageType, Function<String,String>> map = new HashMap<>();
        map.put(MessageType.JOIN, joinGame());
        map.put(MessageType.DISCONNECT, disconnectGame());
        map.put(MessageType.CREATE, createGame());
        map.put(MessageType.START, startGame());
        return map;
    }

    private Function<String, String> createGame() {
        return (message) -> {
            try {
                games.add(Presentation.deserializeAs(message, Game.class));
                gamesCollection.insertOne(convertGameToDocument(getLastGame()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getLastGame().getId();
        };
    }

    private Function<String, String> startGame() {
        return message -> {
            try {
                var startReq = Presentation.deserializeAs(message, StartRequest.class);
                var game = getGameById(startReq.getGameID());
                if(game.isPresent() && game.get().gameCouldStart() && !game.get().isStarted()){
                    if(game.get().hasNextRound()) {
                        game.get().setState(GameState.STARTED);
                        emitter.emit(MessageType.START, Presentation.serializerOf(Game.class).serialize(game.get()));
                        updateDb(game.get());
                        return Presentation.serializerOf(Game.class).serialize(game.get());
                    }else {
                        game.get().setState(GameState.FINISHED);
                        updateDb(game.get());
                        emitter.emit(MessageType.FINISH, game.get().getId());
                        return Presentation.serializerOf(GameScores.class).serialize(game.get().getScores());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };
    }

    private Function<String, String> joinGame() {
        return message -> {
            try {
                var request = Presentation.deserializeAs(message, UserInLobbyRequest.class);
                var game = getGameById(request.getGameID());
                if(game.isPresent() && game.get().addNewUser(request.getUser())){
                    updateDb(game.get());
                    sendGameUpdateToRoundManager(game.get());
                    return Presentation.serializerOf(Game.class).serialize(game.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };
    }


    private Function<String, String> disconnectGame() {
        return message -> {
            try {
                var req = Presentation.deserializeAs(message, DisconnectRequest.class);
                var game = games.stream().filter(g -> g.removeUser(req.getUserAddress())).findFirst();
                if(game.isPresent()){
                    sendGameUpdateToRoundManager(game.get());

                    if(game.get().isFinished() && game.get().getOnlineUsers().isEmpty()){
                        removeFromDb(game.get().getId());
                    }else{
                        updateDb(game.get());
                    }
                    return Presentation.serializerOf(Game.class).serialize(game.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };
    }

    private void sendGameUpdateToRoundManager(Game game){
        if(game.roundIsStarted()) {
            emitter.emit(MessageType.DISCONNECT, Presentation.serializerOf(Game.class).serialize(game));
        }
    }


    private Game getLastGame(){
        return games.get(games.size() - 1);
    }

    private Optional<Game> getGameById(String id){
        return games.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst();
    }

    private void updateDb(Game game){
        gamesCollection.replaceOne(Filters.eq("gameID", game.getId()),convertGameToDocument(game));
    }

    private void removeFromDb(String gameID){
        gamesCollection.deleteOne(Filters.eq("gameID", gameID));
    }


    private Document convertGameToDocument(Game game){
        return Document.parse(Presentation.serializerOf(Game.class).serialize(game));
    }
}
