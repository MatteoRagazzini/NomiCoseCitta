package model.game;

import com.rabbitmq.client.DeliverCallback;
import model.request.DisconnectRequest;
import model.request.UserInLobbyRequest;
import model.request.StartRequest;
import presentation.Presentation;

import rabbit.Consumer;
import rabbit.Emitter;
import rabbit.MessageType;
import rabbit.RPCServer;

import java.util.*;
import java.util.function.Function;

public class GameManager {

    private final List<Game> games;
    private final Emitter emitter;

    public GameManager() {
        games = new ArrayList<>();
        emitter = new Emitter("game");
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Aggiunto un gioco " + getLastGame());
            return getLastGame().getId();
        };
    }

    private Function<String, String> startGame() {
        return message -> {
            try {
                var startReq = Presentation.deserializeAs(message, StartRequest.class);
                var game = getGameById(startReq.getGameID());
                if(game.isPresent() && game.get().gameCouldStart() && !game.get().isStarted()){
                    game.get().setState(GameState.STARTED);
                    emitter.emit(MessageType.START, Presentation.serializerOf(Game.class).serialize(game.get()));
                    return Presentation.serializerOf(Game.class).serialize(game.get());
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
                    return Presentation.serializerOf(Game.class).serialize(game.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };
    }

    private void sendGameUpdateToRoundManager(Game game){
        if(game.isStarted()) {
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
}
