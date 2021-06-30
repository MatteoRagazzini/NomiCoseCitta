package model.request;

public class DisconnectRequest {
    private final String userAddress;

    public DisconnectRequest(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserAddress() {
        return userAddress;
    }
}
