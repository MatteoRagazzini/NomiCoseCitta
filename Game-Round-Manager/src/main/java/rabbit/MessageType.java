package rabbit;

public enum MessageType {
    CREATE("create"),
    JOIN("join"),
    START("start"),
    WORDS("words"),
    STOP("stop"),
    VOTES("votes"),
    SCORES("scores"),
    CHECK("check"),
    FINISH("finish"),
    UPDATE("update"),
    DISCONNECT("disconnect");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
