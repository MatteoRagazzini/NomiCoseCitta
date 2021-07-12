package model.request;

public class StartRequest {
    private final String gameID;

    public StartRequest(String gameID) {
        this.gameID = gameID;
    }

    public String getGameID() {
        return gameID;
    }
}

