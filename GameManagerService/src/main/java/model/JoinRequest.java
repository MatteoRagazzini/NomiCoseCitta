package model;

public class JoinRequest {

    private User user;
    private String gameID;

    public JoinRequest() {
    }

    public JoinRequest(User user, String gameID) {
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
