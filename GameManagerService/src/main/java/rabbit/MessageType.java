package rabbit;

public enum MessageType {
    CREATE("create"), ACCESS("access"), START("start");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
