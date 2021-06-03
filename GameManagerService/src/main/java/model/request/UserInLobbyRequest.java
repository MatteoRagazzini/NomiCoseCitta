package model.request;

import model.User;

public class UserInLobbyRequest {

    private User user;
    private String gameID;

    public UserInLobbyRequest() {
    }

    public UserInLobbyRequest(User user, String gameID) {
        this.user = user;
        this.gameID = gameID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    @Override
    public String toString() {
        return "JoinRequest{" +
                "user='" + user + '\'' +
                ", gameID=" + gameID +
                '}';
    }
}
