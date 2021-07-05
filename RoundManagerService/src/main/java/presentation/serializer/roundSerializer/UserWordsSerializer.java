package presentation.serializer.roundSerializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import model.round.words.UserWords;
import presentation.serializer.AbstractJsonSerializer;

public class UserWordsSerializer extends AbstractJsonSerializer<UserWords> {
    @Override
    protected JsonElement toJsonElement(UserWords object) {
        var jobj = new JsonObject();
        jobj.addProperty("userID", object.getUserID());
        jobj.addProperty("gameID", object.getGameID());
        object.getWords().forEach(jobj::addProperty);
        return jobj;
    }
}
