package rabbitMQ;

public enum MessageType {
    CREATE("create"), JOIN("join"), START("start");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
