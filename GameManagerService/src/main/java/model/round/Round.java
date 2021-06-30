package model.round;

import model.game.Game;
import model.round.words.Evaluation;
import model.round.words.RoundWords;
import model.round.words.UserWords;

public abstract class Round {

    private Game game;
    private RoundState state;
   private final RoundWords roundWords;

    public Round(Game game) {
        this.game = game;
        state = RoundState.PLAY;
        roundWords = new RoundWords(game.getOnlineUsers(), game.getUsers(), game.getPlayedRounds()+1);
        onStart();
    }

    public Game getGame() {
        return game;
    }

    public void updateGame(Game newGame){
        this.game= newGame;
        this.roundWords.updateUserOnline(newGame.getOnlineUsers());
    }

    public RoundState getState() {
        return state;
    }

    public RoundWords getRoundWords() {
        return roundWords;
    }

    public RoundScores getRoundScores(){
        return ScoreCalculator.calculateScores(roundWords);
    }

    public void insertUserWord(UserWords userWords){
        roundWords.insertUserWords(userWords);
    }

    public void insertEvaluation(Evaluation evaluation){
        roundWords.insertEvaluation(evaluation);
    }

    public boolean scoresAvailable() {
        return roundWords.allEvaluationAreDelivered();
    }

    abstract void onStart();
    abstract void onStop();

    public void setState(RoundState state) {
        this.state = state;
    }
}
