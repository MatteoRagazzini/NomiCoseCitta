import rabbitMQ.MessageType;

public class Logger {
    public static void log(MessageType messageType, String message){
        System.out.println(" [x] Receive '" + messageType.getType() + "':'" + message + "'");
    }
}
