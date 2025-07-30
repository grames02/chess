package model;

import java.util.List;

public class ListGamesResponse {
    private List<GameData> games;

    public List<GameData> getGames() {
        return games;
    }
    public ListGamesResponse(List<GameData> games) {
        this.games = games;
    }
}
