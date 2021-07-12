package presentation.deserializer.roundDeserializer;

import com.google.gson.JsonElement;
import model.round.RoundScores;
import model.round.UserRoundScore;
import presentation.Presentation;
import presentation.deserializer.AbstractJsonDeserializer;

import java.util.ArrayList;
import java.util.List;

public class RoundScoresDeserializer extends AbstractJsonDeserializer<RoundScores> {
    @Override
    protected RoundScores deserializeJson(JsonElement jsonElement) {
        if(jsonElement.isJsonObject()){
            var jobj = jsonElement.getAsJsonObject();
            if(jobj.has("usersScores") && jobj.has("roundNumber")){
                List<UserRoundScore> listUsersScores = new ArrayList<>();
                jobj.get("usersScores").getAsJsonArray().forEach(us -> {
                    try {
                        listUsersScores.add(Presentation.deserializeAs(us.toString(), UserRoundScore.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return new RoundScores(listUsersScores, jobj.get("roundNumber").getAsInt());
            }
        }
        throw new RuntimeException("Cannot deserialize as RoundScores");
    }
}
