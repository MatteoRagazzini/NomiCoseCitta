package presentation.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.game.GameSettings;

public class GameSettingsSerializer extends AbstractJsonSerializer<GameSettings> {

    @Override
    protected JsonElement toJsonElement(GameSettings object) {
        var jsonObject = new JsonObject();
        var categories = new JsonArray();
        var letters = new JsonArray();
        object.getCategories().forEach(categories::add);
        object.getRoundsLetter().forEach(letters::add);
        jsonObject.add("categories", categories);
        jsonObject.add("roundsLetters", letters);
        jsonObject.addProperty("roundsType", object.getRoundType().toString());
        jsonObject.addProperty("numRounds", object.getNumberOfRounds());
        jsonObject.addProperty("numUsers", object.getNumberOfUsers());
        return jsonObject;
    }
}
