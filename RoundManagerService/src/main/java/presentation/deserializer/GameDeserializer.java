package presentation.deserializer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.User;
import model.game.Game;
import model.game.GameIDSupplier;
import model.game.GameSettings;
import model.builder.GameBuilder;
import presentation.Presentation;

import java.util.ArrayList;
import java.util.List;

public class GameDeserializer extends AbstractJsonDeserializer<Game> {

    @Override
    protected Game deserializeJson(JsonElement jsonElement) {
        GameBuilder builder = new GameBuilder();
        if(jsonElement instanceof JsonObject){
            var jobj = (JsonObject) jsonElement;
            try {
                if(jobj.has("users") && jobj.get("users").isJsonArray()){
                    List<User> usersList = new ArrayList<>();
                    jobj.get("users").getAsJsonArray().forEach(u -> {
                        try {
                            usersList.add(Presentation.deserializeAs(u.toString(), User.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    builder.setUsers(usersList);
                }
                if(jobj.has("fixedUsers") && jobj.get("fixedUsers").isJsonArray()){
                    List<User> usersList = new ArrayList<>();
                    jobj.get("fixedUsers").getAsJsonArray().forEach(u -> {
                        try {
                            usersList.add(Presentation.deserializeAs(u.toString(), User.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    builder.seiFixedUser(usersList);
                }
                if(jobj.has("couldStart") && jobj.get("couldStart").isJsonPrimitive()){
                    builder.setIsStarted(jobj.get("couldStart").getAsBoolean());
                }
                if(jobj.has("gameID") && jobj.get("gameID").isJsonPrimitive()){
                    builder.setGameID(jobj.get("gameID").getAsString());
                }else{
                    builder.setGameID(GameIDSupplier.getInstance().getNewGameID());
                }
                if(jobj.has("gameState") && jobj.get("gameState").isJsonPrimitive()){
                    builder.setState(jobj.get("gameState").getAsString());
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
