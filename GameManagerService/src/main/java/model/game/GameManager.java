package model.game;

import model.User;
import model.request.DisconnectRequest;
import model.request.UserInLobbyRequest;
import model.request.StartRequest;
import presentation.Presentation;

import rabbit.Emitter;
import rabbit.MessageType;
import rabbit.RPCServer;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GameManager {

    private final List<Game> games;
    private final Emitter emitter;

    public GameManager() {
        games = new ArrayList<>();
        emitter = new Emitter("game");
        new RPCServer(getCallbackMap());
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
                    return Presentation.serializerOf(GameSettings.class).serialize(game.get().getSettings());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };
    }

    private Function<String, String> lobbyRequest(BiFunction<User, Game, Boolean> requestHandler){
        return message -> {
            try {
                var request = Presentation.deserializeAs(message, UserInLobbyRequest.class);
                var game = getGameById(request.getGameID());
                if(game.isPresent() && requestHandler.apply(request.getUser(), game.get())){
                    return Presentation.serializerOf(Game.class).serialize(game.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };
    }

    private Function<String, String> joinGame() {
        return lobbyRequest((user, game) -> !game.isFull() &&  game.addNewUser(user));
    }

//    private Function<String, String> disconnectGame() {
//        return lobbyRequest((user, game) -> game.removeUser(user));
//    }

    private Function<String, String> disconnectGame() {
        return message -> {
            try {
                var req = Presentation.deserializeAs(message, DisconnectRequest.class);
                var game = games.stream().filter(g -> g.removeUser(req.getUserAddress())).findFirst();
                if(game.isPresent()){
                    return Presentation.serializerOf(Game.class).serialize(game.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };
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
