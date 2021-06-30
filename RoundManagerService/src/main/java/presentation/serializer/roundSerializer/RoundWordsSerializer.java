package presentation.serializer.roundSerializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.round.words.RoundWords;
import model.round.words.UserWords;
import presentation.Presentation;
import presentation.serializer.AbstractJsonSerializer;

public class RoundWordsSerializer extends AbstractJsonSerializer<RoundWords> {
    @Override
    protected JsonElement toJsonElement(RoundWords object) {
        var jsonObject = new JsonObject();
        var usersArray = new JsonArray();
        object.getUsersWords().forEach(uw -> usersArray.add(Presentation.serializerOf(UserWords.class).getJsonElement(uw)));
        jsonObject.add("usersWords", usersArray);
        return jsonObject;
    }
}
