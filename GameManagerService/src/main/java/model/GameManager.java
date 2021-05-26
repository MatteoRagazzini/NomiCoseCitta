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
    //private final Consumer createGameConsumer;
    private final RPCServer createGameServer;

    public GameManager() {
        games = new ArrayList<>();
        createGameServer = new RPCServer(createGame(), MessageType.CREATE);
        //createGameConsumer = new Consumer(createGame(), MessageType.CREATE);
    }

    //    private DeliverCallback createGame(){
//        return (consumerTag, message) -> {
//            System.out.println("IN CALLBACK");
//            String msg = new String(message.getBody(), "UTF-8");
//            try {
//                games.add(Presentation.deserializeAs(msg, Game.class));
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Impossibile deserializzare il gioco");
//            }
//
//            System.out.println("Aggiunto un gioco " + games.get(games.size()-1).toString());
//        };
//    }
    private Function<String, String> createGame() {
        return (message) -> {
            System.out.println("IN CALLBACK");
            try {
                games.add(Presentation.deserializeAs(message, Game.class));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Impossibile deserializzare il gioco");
            }

            System.out.println("Aggiunto un gioco " + games.get(games.size() - 1).toString());
            return "NUOVO GIOCO";
        };
    }
}
