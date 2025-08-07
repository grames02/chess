package model;

public class ConnectRequest {
    private final String authToken;
    private final int gameId;

    public ConnectRequest(String authToken, int gameId) {
        this.authToken = authToken;
        this.gameId = gameId;
    }
}
