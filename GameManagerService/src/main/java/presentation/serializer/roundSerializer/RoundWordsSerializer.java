package presentation.serializer.roundSerializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.game.GameSettings;
import model.round.RoundWords;
import model.round.UserWords;
import presentation.Presentation;
import presentation.serializer.AbstractJsonSerializer;

public class RoundWordsSerializer extends AbstractJsonSerializer<RoundWords> {
    @Override
    protected JsonElement toJsonElement(RoundWords object) {
        var jsonObject = new JsonObject();
        var usersArray = new JsonArray();
        object.getUsersWords().forEach(uw -> usersArray.add(Presentation.serializerOf(UserWords.class).getJsonElement(uw)));
        jsonObject.add("usersWords", usersArray);
        jsonObject.addProperty("userNumber", object.getUserNumber());
        return jsonObject;
    }
}
