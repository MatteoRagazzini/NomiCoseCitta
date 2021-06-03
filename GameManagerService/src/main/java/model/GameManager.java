package model;

import model.request.JoinRequest;
import model.request.StartRequest;
import presentation.Presentation;
import rabbit.MessageType;
import rabbit.RPCServer;

import java.util.*;
import java.util.function.Function;

public class GameManager {

    private final List<Game> games;
    private final RPCServer joinGameServer;

    public GameManager() {
        games = new ArrayList<>();
        //createGameServer = new RPCServer(createGame(), MessageType.CREATE);
        joinGameServer = new RPCServer(getCallbackMap());
    }

    private Map<MessageType, Function<String,String>> getCallbackMap(){
        Map<MessageType, Function<String,String>> map = new HashMap<>();
        map.put(MessageType.JOIN, joinGame());
        map.put(MessageType.CREATE, createGame());
        map.put(MessageType.START, startGame());
        return map;
    }

    private Function<String, String> startGame() {
        return message -> {
            try {
                var startReq = Presentation.deserializeAs(message, StartRequest.class);
                var game = getGameById(startReq.getGameID());
                if(game.isPresent() && game.get().gameCouldStart() && !game.get().isStarted()){
                    game.get().setState(GameState.STARTED);
                    return Presentation.serializerOf(GameSettings.class).serialize(game.get().getSettings());
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
                var joinReq = Presentation.deserializeAs(message, JoinRequest.class);
                var game = getGameById(joinReq.getGameID());
                if(game.isPresent() && !game.get().isFull()){
                    game.get().addNewUser(joinReq.getUser());
                    return Presentation.serializerOf(Game.class).serialize(game.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "null";
        };
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

    private Game getLastGame(){
        return games.get(games.size() - 1);
    }

    private Optional<Game> getGameById(String id){
        return games.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst();
    }
}
