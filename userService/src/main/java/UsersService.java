import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class UsersService {
	

    private final static String QUEUE_NAME = "users";
    private int usersOnline = 0;
    
    public void newUserOnline() {
    	usersOnline++;
    }
    
    public void userDisconected() {
    	usersOnline--;
    }

    public int getUsersOnline() {
		return usersOnline;
	}

	public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        UsersService service = new UsersService();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            if(message.equals("new")) {
            	service.newUserOnline();
            }else if(message.equals("closed")) {
            	service.userDisconected();
            }
            System.out.println("Number of users online: " + service.getUsersOnline());
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }

}
