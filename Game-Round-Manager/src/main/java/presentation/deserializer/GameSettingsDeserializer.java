package presentation.deserializer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.game.GameSettings;
import model.round.RoundType;
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
            if(jobj.has("roundsType") && jobj.get("roundsType").isJsonPrimitive()){
                builder.setRoundType(jobj.get("roundsType").getAsString().equals(RoundType.STOP.toString()) ?
                        RoundType.STOP : RoundType.TIMER);
            }
            if(jobj.has("categories") && jobj.get("categories").isJsonArray()){
                builder.setCategories(new Gson().fromJson(jobj.getAsJsonArray("categories"), ArrayList.class));
            }
            if(jobj.has("roundsLetters") && jobj.get("roundsLetters").isJsonArray()){
                builder.setRoundsLetters(new Gson().fromJson(jobj.getAsJsonArray("roundsLetters"), ArrayList.class));
            }
            if(jobj.has("numUsers") && jobj.get("numUsers").isJsonPrimitive()){
                builder.setNumberOfUsers(jobj.get("numUsers").getAsInt());
            }

        }
        return builder.build();
    }
}
