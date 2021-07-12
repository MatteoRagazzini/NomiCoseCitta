package model.game;

public class GameIDSupplier {

    private static GameIDSupplier instance;
    private Integer gameCreated = 0;

    private GameIDSupplier() {}

    public static GameIDSupplier getInstance() {
        if (instance == null) {
            instance = new GameIDSupplier();
        }
        return instance;
    }

    public void setGameCreated(int gameCreated){
        this.gameCreated = gameCreated;
    }

    public String getNewGameID(){
        gameCreated++;
        return String.valueOf(gameCreated);
    }
}
