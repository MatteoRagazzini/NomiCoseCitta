package model;

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

    public String getNewGameID(){
        gameCreated++;
        return "Game" + gameCreated;
    }
}
