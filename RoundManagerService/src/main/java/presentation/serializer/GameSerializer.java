package presentation.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.User;
import model.game.Game;
import model.game.GameSettings;
import presentation.Presentation;

public class GameSerializer extends AbstractJsonSerializer<Game>{
    @Override
    protected JsonElement toJsonElement(Game object) {
        var jsonObject = new JsonObject();
        var usersArray = new JsonArray();
        var fixedUser = new JsonArray();
        object.getUsers().forEach(u -> fixedUser.add(Presentation.serializerOf(User.class).getJsonElement(u)));
        object.getOnlineUsers().forEach(u -> usersArray.add(Presentation.serializerOf(User.class).getJsonElement(u)));
        jsonObject.add("fixedUsers", fixedUser);
        jsonObject.add("users", usersArray);
        jsonObject.addProperty("couldStart", object.gameCouldStart());
        jsonObject.addProperty("gameState", object.getState().toString());
        jsonObject.addProperty("gameID", object.getId());
        jsonObject.addProperty("playedRounds", object.getPlayedRounds());
        jsonObject.add("settings", Presentation.serializerOf(GameSettings.class).getJsonElement(object.getSettings()));
        return jsonObject;
    }
}
