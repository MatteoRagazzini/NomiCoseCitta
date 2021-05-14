package presentation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import model.Game;
import model.GameSettings;
import model.User;
import model.builder.GameBuilder;

public class GameDeserializer extends AbstractJsonDeserializer<Game> {
    @Override
    protected Game deserializeJson(JsonElement jsonElement) {
        GameBuilder builder = new GameBuilder();
        if(jsonElement instanceof JsonObject){
            var jobj = (JsonObject) jsonElement;
            if(jobj.has("gameID") && jobj.get("gameID").isJsonPrimitive()){
                builder.setGameID(jobj.get("gameID").getAsString());
            }
            if(jobj.has("userID") && jobj.get("userID").isJsonPrimitive()){
                builder.setCreator(new User(jobj.get("userID").getAsString()));
            }
            try {
                    builder.setSettings(Presentation.deserializeAs(
                            jobj.toString(), GameSettings.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return builder.build();
    }
}
