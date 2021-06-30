package model.round;

public class WordScore {
    private final String word;
    private final Integer score;

    public WordScore(String word, Integer score) {
        this.word = word;
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return "WordScore{" +
                "word='" + word + '\'' +
                ", score=" + score +
                '}';
    }
}
