package presentation.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.GameSettings;

public class GameSettingsSerializer extends AbstractJsonSerializer<GameSettings> {

    @Override
    protected JsonElement toJsonElement(GameSettings object) {
        var jsonObject = new JsonObject();
        var categories = new JsonArray();
        object.getCategories().forEach(categories::add);
        jsonObject.add("categories", categories);
        jsonObject.addProperty("roundType", object.getRoundType().toString());
        jsonObject.addProperty("roundsNumber", object.getNumberOfRounds());
        return jsonObject;
    }
}
