package model;

import com.rabbitmq.client.DeliverCallback;
import presentation.Presentation;
import rabbit.Consumer;
import rabbit.MessageType;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private final List<Game> games;
    private final Consumer createGameConsumer;

    public GameManager() {
        games = new ArrayList<>();
        createGameConsumer = new Consumer(createGame(), MessageType.CREATE);
    }

    private DeliverCallback createGame(){
        return (consumerTag, message) -> {
            System.out.println("IN CALLBACK");
            String msg = new String(message.getBody(), "UTF-8");
            try {
                games.add(Presentation.deserializeAs(msg, Game.class));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Impossibile deserializzare il gioco");
            }

            System.out.println("Aggiunto un gioco " + games.get(games.size()-1).toString());
        };
    }

}
