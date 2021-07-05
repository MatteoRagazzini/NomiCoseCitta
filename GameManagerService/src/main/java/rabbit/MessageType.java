package rabbit;

public enum MessageType {
    CREATE("create", "create_response"),
    JOIN("join", "join_response"),
    START("start", "start_response"),
    WORDS("words", "words_response"),
    STOP("stop", "stop_response"),
    VOTES("votes", "votes_response"),
    SCORES("scores", "scores_response"),
    CHECK("check", "check_response"),
    FINISH("finish", "finish_response"),
    DISCONNECT("disconnect", "disconnect_response");

    private final String type;
    private final String responseType;

    MessageType(String type, String responseType) {
        this.type = type;
        this.responseType = responseType;
    }

    public String getType() {
        return type;
    }

    public String getResponseType() {
        return responseType;
    }
}
