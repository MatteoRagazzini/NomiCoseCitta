package presentation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.Game;

public class GameStatusSerializer extends AbstractJsonSerializer<Game>{
    @Override
    protected JsonElement toJsonElement(Game object) {
        var jsonObject = new JsonObject();
        var usersArray = new JsonArray();
        object.getUsers().forEach(u -> usersArray.add(u.getNickname()));
        jsonObject.add("users", usersArray);
        jsonObject.addProperty("couldStart", object.gameCouldStart());
        return jsonObject;
    }
}
