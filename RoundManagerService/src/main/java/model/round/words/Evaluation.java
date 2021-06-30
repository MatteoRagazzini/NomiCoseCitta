package model.round.words;

import java.util.List;

public class Evaluation {
    private final String gameID;
    private final String voterID;
    private final List<Vote> votes;

    public Evaluation(String gameID, String voterID, List<Vote> v) {
        this.gameID = gameID;
        this.voterID = voterID;
        this.votes = v;
    }

    public String getGameID() {
        return gameID;
    }

    public String getVoterID() {
        return voterID;
    }

    public List<Vote> getVotes() {
        return votes;
    }
}
