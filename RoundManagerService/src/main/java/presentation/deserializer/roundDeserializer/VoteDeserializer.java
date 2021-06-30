package presentation.deserializer.roundDeserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.round.words.Vote;
import presentation.deserializer.AbstractJsonDeserializer;

public class VoteDeserializer extends AbstractJsonDeserializer<Vote> {
    @Override
    protected Vote deserializeJson(JsonElement jsonElement) {
        if(jsonElement.isJsonObject()){
            JsonObject jobj = jsonElement.getAsJsonObject();
            if(jobj.has("userID")){
                Vote v = new Vote(jobj.get("userID").getAsString());
                jobj.remove("userID");
                jobj.entrySet().forEach(e -> v.insertVote(e.getKey(), e.getValue().getAsString()));
                return v;
            }
        }
        throw new RuntimeException("Cannot deserialize as Vote");
    }
}
