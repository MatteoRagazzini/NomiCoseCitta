package model;

import com.rabbitmq.client.DeliverCallback;
import presentation.Presentation;
import rabbit.Consumer;
import rabbit.MessageType;
import rabbit.RPCServer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GameManager {

    private final List<Game> games;
    private final RPCServer createGameServer;

    public GameManager() {
        games = new ArrayList<>();
        createGameServer = new RPCServer(createGame(), MessageType.CREATE);
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
