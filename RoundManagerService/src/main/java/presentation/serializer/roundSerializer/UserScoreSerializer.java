package presentation.serializer.roundSerializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.round.UserScore;
import presentation.serializer.AbstractJsonSerializer;

public class UserScoreSerializer extends AbstractJsonSerializer<UserScore> {
    @Override
    protected JsonElement toJsonElement(UserScore object) {
        JsonObject jobj = new JsonObject();
        JsonArray jarr = new JsonArray();
        jobj.addProperty("userID", object.getUserID());
        object.getScores().forEach((category, wordScore) -> {
            JsonObject score = new JsonObject();
            score.addProperty("category", category);
            score.addProperty("word", wordScore.getWord());
            score.addProperty("score", wordScore.getScore());
            jarr.add(score);
        });
        jobj.add("wordsScores", jarr );
        jobj.addProperty("total", object.getTotalScore());
        return jobj;
    }
}
