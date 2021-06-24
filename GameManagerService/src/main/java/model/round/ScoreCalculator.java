package model.round;

import model.round.words.RoundWords;
import model.round.words.UserWords;

import java.util.stream.Collectors;

public class ScoreCalculator {

    private final static Integer UNIQUE_WORD_SCORE = 20;
    private final static Integer VALID_WORD_SCORE = 10;
    private final static Integer COMMON_WORD_SCORE = 5;
    private final static Integer NON_VALID_WORD_SCORE = 0;

    public static RoundScores calculateScores(RoundWords rw){
        return new RoundScores(rw.getUsersWords().stream()
                .map(u -> calculateUserScore(rw, u))
                .collect(Collectors.toList()));
    }

    private static UserScore calculateUserScore(RoundWords rw, UserWords userWords){
        var userScore = new UserScore(userWords.getUserID());
        userWords.getWords().forEach((category, word) -> {
            Integer wordScore =
                    isValidWord(category, userWords, rw.getNumberOfOnlineUser()/2) ?
                            isUniqueWord(category, rw) ? UNIQUE_WORD_SCORE :
                                    isCommonWord(category, word, rw) ? COMMON_WORD_SCORE : VALID_WORD_SCORE
                            : NON_VALID_WORD_SCORE;

            userScore.addWordScore(category, new WordScore(word,wordScore));
        });
        return userScore;
    }

    private static Boolean isValidWord(String category, UserWords uw, Integer factorValidWord){
        return !uw.getWords().get(category).isEmpty() && uw.getVotes().get(category) > factorValidWord;
    }

    private static Boolean isCommonWord(String category, String word, RoundWords rw){
        return rw.getUsersWords().stream()
                .map(u -> u.getWords().get(category))
                .filter(w -> w.equals(word)).count() > 1;
    }

    //is the only word write for the category
    private static Boolean isUniqueWord(String category, RoundWords rw){
        return rw.getUsersWords().stream()
                .map(u -> u.getWords().get(category))
                .filter(w -> !w.isEmpty()).count() == 1;
    }
}
