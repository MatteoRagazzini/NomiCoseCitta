package model.request;

import model.User;

public class JoinRequest {

    private String userAddress;
    private String userID;
    private String gameID;

    public JoinRequest() {
    }

    public JoinRequest(String userID, String address, String gameID) {
        this.userAddress = address;
        this.gameID = gameID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public User getUser(){
        return new User(userID, userAddress);
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
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
                "user='" + getUser() + '\'' +
                ", gameID=" + gameID +
                '}';
    }
}
