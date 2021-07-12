package presentation.deserializer.roundDeserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.round.UserRoundScore;
import model.round.WordScore;
import presentation.deserializer.AbstractJsonDeserializer;

public class UserScoreDeserializer extends AbstractJsonDeserializer<UserRoundScore> {
    @Override
    protected UserRoundScore deserializeJson(JsonElement jsonElement) {
        if(jsonElement.isJsonObject()){
            JsonObject jobj = jsonElement.getAsJsonObject();
            if(jobj.has("userID")){
                var userScore = new UserRoundScore(jobj.get("userID").getAsString());
                if(jobj.has("wordsScores")){
                    jobj.get("wordsScores").getAsJsonArray().forEach(je -> {
                        if(je.isJsonObject()){
                            var jws = je.getAsJsonObject();
                            if(jws.has("category") &&
                                    jws.has("word") && jws.has("score")){
                                userScore.addWordScore(jws.get("category").getAsString(),
                                        new WordScore(jws.get("word").getAsString(), jws.get("score").getAsInt()));
                            }
                        }
                    });
                }
                return userScore;
            }
        }

        throw new RuntimeException("Cannot deserialize as UserScore");
    }
}
