package presentation.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.User;
import model.game.Game;
import model.game.GameSettings;
import model.round.RoundScores;
import presentation.Presentation;

public class GameSerializer extends AbstractJsonSerializer<Game>{
    @Override
    protected JsonElement toJsonElement(Game object) {
        var jsonObject = new JsonObject();
        var usersArray = new JsonArray();
        var fixedUser = new JsonArray();
        var gameScores = new JsonArray();
        object.getUsers().forEach(u -> fixedUser.add(Presentation.serializerOf(User.class).getJsonElement(u)));
        object.getOnlineUsers().forEach(u -> usersArray.add(Presentation.serializerOf(User.class).getJsonElement(u)));
        object.getScores().forEach(r -> gameScores.add(Presentation.serializerOf(RoundScores.class).getJsonElement(r)));
        jsonObject.add("fixedUsers", fixedUser);
        jsonObject.add("users", usersArray);
        jsonObject.add("gameScores", gameScores);
        jsonObject.addProperty("couldStart", object.gameCouldStart());
        jsonObject.addProperty("gameState", object.getState().toString());
        jsonObject.addProperty("gameID", object.getId());
        jsonObject.addProperty("playedRounds", object.getPlayedRounds());
        jsonObject.add("settings", Presentation.serializerOf(GameSettings.class).getJsonElement(object.getSettings()));
        return jsonObject;
    }
}
