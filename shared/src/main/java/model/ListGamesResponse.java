package model;

import java.util.Collection;
import java.util.List;

public class ListGamesResponse {
    private List<GameData> games;
    public List<GameData> getGames() {
        return games;
    }
    public void setGames(List<GameData> games) {
        this.games = games;
    }
}
