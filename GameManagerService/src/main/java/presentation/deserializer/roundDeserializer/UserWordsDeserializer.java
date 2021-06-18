package presentation.deserializer.roundDeserializer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.builder.GameSettingsBuilder;
import model.round.RoundType;
import model.round.UserWords;
import presentation.deserializer.AbstractJsonDeserializer;

import java.util.ArrayList;

public class UserWordsDeserializer extends AbstractJsonDeserializer<UserWords> {
    @Override
    protected UserWords deserializeJson(JsonElement jsonElement) {
        GameSettingsBuilder builder = new GameSettingsBuilder();
        if(jsonElement instanceof JsonObject){
            var jobj = (JsonObject) jsonElement;
            if(jobj.has("userID") && jobj.get("userID").isJsonPrimitive()
            && jobj.has("gameID") && jobj.get("gameID").isJsonPrimitive() ){
               var uw = new UserWords(jobj.get("userID").getAsString(), jobj.get("gameID").getAsString());
               jobj.remove("gameID");
               jobj.remove("userID");
               jobj.entrySet().forEach(e -> uw.insertWord(e.getKey(), e.getValue().getAsString()));
               return uw;
            }

        }
        throw new RuntimeException("Cannot deserialize this message as userWords");
    }
}
