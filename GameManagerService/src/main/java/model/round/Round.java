package model.round;

import model.game.Game;

public abstract class Round {

    private Game game;
    private RoundState state;
   private final RoundWords usersWords;

    public Round(Game game) {
        this.game = game;
        state = RoundState.PLAY;
        usersWords = new RoundWords(game.getOnlineUsers(), game.getUsers());
        onStart();
    }

    public Game getGame() {
        return game;
    }

    public void updateGame(Game newGame){
        this.game= newGame;
    }

    public RoundState getState() {
        return state;
    }

    public RoundWords getUsersWords() {
        return usersWords;
    }

    public void insertUserWord(UserWords userWords){
        usersWords.insertUserWords(userWords);
    }

    abstract void onStart();
    abstract void onStop();

    public void setState(RoundState state) {
        this.state = state;
    }
}
