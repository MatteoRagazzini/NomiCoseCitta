package model;

import com.rabbitmq.client.DeliverCallback;
import presentation.Presentation;
import rabbit.Consumer;
import rabbit.MessageType;
import rabbit.RPCServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GameManager {

    private final List<Game> games;
    private  RPCServer createGameServer;
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
        return map;
    }

    private Function<String, String> joinGame() {
        return message -> {
            System.out.println(message);
            return "yes";
        };
    }

    private Function<String, String> createGame() {
        return (message) -> {
            System.out.println("IN CALLBACK");
            try {
                games.add(Presentation.deserializeAs(message, Game.class));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Impossibile deserializzare il gioco");
            }

            System.out.println("Aggiunto un gioco " + getLastGame());
            return getLastGame().getId();
        };
    }

    private Game getLastGame(){
        return games.get(games.size() - 1);
    }
}
