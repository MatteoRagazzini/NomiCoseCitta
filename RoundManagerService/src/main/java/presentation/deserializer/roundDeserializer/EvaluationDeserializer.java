package presentation.deserializer.roundDeserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.round.words.Evaluation;
import model.round.words.Vote;
import presentation.Presentation;
import presentation.deserializer.AbstractJsonDeserializer;

import java.util.ArrayList;
import java.util.List;

public class EvaluationDeserializer extends AbstractJsonDeserializer<Evaluation> {
    @Override
    protected Evaluation deserializeJson(JsonElement jsonElement) {
        if(jsonElement.isJsonObject()){
            JsonObject jobj = jsonElement.getAsJsonObject();
            if(jobj.has("gameID") && jobj.has("voterID") && jobj.has("votes")){
                List<Vote> votes = new ArrayList<>();
                jobj.get("votes").getAsJsonArray().forEach(v -> {
                    try {
                        votes.add(Presentation.deserializeAs(v.toString(), Vote.class));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return new Evaluation(jobj.get("gameID").getAsString(), jobj.get("voterID").getAsString(), votes);
            }
        }
        throw new RuntimeException("Cannot deserialize as Evaluation");
    }
}
