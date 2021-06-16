package presentation.deserializer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.game.Game;
import model.game.GameIDSupplier;
import model.game.GameSettings;
import model.builder.GameBuilder;
import presentation.Presentation;

import java.util.ArrayList;

public class GameDeserializer extends AbstractJsonDeserializer<Game> {

    @Override
    protected Game deserializeJson(JsonElement jsonElement) {
        GameBuilder builder = new GameBuilder();
        if(jsonElement instanceof JsonObject){
            var jobj = (JsonObject) jsonElement;
            try {
                if(jobj.has("users") && jobj.get("users").isJsonArray()){
                    builder.setUsers(new Gson().fromJson(jobj.get("users").getAsJsonArray(), ArrayList.class));
                }
                if(jobj.has("couldStart") && jobj.get("couldStart").isJsonPrimitive()){
                    builder.setIsStarted(jobj.get("couldStart").getAsBoolean());
                }
                if(jobj.has("gameID") && jobj.get("gameID").isJsonPrimitive()){
                    builder.setGameID(jobj.get("gameID").getAsString());
                }else{
                    builder.setGameID(GameIDSupplier.getInstance().getNewGameID());
                }
                if(jobj.has("settings")){
                    builder.setSettings(Presentation.deserializeAs(
                            jobj.get("settings").toString(), GameSettings.class));
                }else {
                    builder.setSettings(Presentation.deserializeAs(
                            jobj.toString(), GameSettings.class));
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return builder.build();
    }
}
