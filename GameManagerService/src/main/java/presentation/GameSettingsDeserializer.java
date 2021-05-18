package presentation;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.GameSettings;
import model.RoundType;
import model.builder.GameSettingsBuilder;

import java.util.ArrayList;

public class GameSettingsDeserializer extends AbstractJsonDeserializer<GameSettings> {
    @Override
    protected GameSettings deserializeJson(JsonElement jsonElement) {
        GameSettingsBuilder builder = new GameSettingsBuilder();
        if(jsonElement instanceof JsonObject){
            var jobj = (JsonObject) jsonElement;
            if(jobj.has("numRounds") && jobj.get("numRounds").isJsonPrimitive()){
                builder.setNumberOfRounds(jobj.get("numRounds").getAsInt());
            }
            if(jobj.has("typeOfRounds") && jobj.get("typeOfRounds").isJsonPrimitive()){
                builder.setRoundType(jobj.get("typeOfRounds").getAsString().equals("first_finish") ?
                        RoundType.STOP : RoundType.TIMER);
            }
            if(jobj.has("categories") && jobj.get("categories").isJsonArray()){
                builder.setCategories(new Gson().fromJson(jobj.get("categories").getAsJsonArray(), ArrayList.class));
            }
            if(jobj.has("numUsers") && jobj.get("numUsers").isJsonPrimitive()){
                builder.setNumberOfUsers(jobj.get("numUsers").getAsInt());
            }

        }
        return builder.build();
    }
}
