package rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {

    private static final String EXCHANGE_NAME = "Web";
    private final DeliverCallback callback;
    private final MessageType msgType;

    public Consumer(DeliverCallback callback, MessageType type) {
        this.callback = callback;
        this.msgType = type;
        try {
            start();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void start() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, msgType.getType());

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        channel.basicConsume(queueName, true, callback, consumerTag -> { });
    }

}
