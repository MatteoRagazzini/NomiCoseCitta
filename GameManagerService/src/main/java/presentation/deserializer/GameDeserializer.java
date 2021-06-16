package presentation.deserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.Game;
import model.GameIDSupplier;
import model.GameSettings;
import model.User;
import model.builder.GameBuilder;
import presentation.Presentation;

public class GameDeserializer extends AbstractJsonDeserializer<Game> {

    @Override
    protected Game deserializeJson(JsonElement jsonElement) {
        GameBuilder builder = new GameBuilder();
        if(jsonElement instanceof JsonObject){
            var jobj = (JsonObject) jsonElement;
            try {
                    builder.setSettings(Presentation.deserializeAs(
                            jobj.toString(), GameSettings.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        builder.setGameID(GameIDSupplier.getInstance().getNewGameID());
        return builder.build();
    }
}
