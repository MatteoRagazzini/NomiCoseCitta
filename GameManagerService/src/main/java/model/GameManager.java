package model;

import com.rabbitmq.client.DeliverCallback;
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
            String msg = new String(message.getBody(), "UTF-8");
            System.out.println(" [x] Received '" +
                    message.getEnvelope().getRoutingKey() + "':'" + msg + "'");
        };
    }

}
