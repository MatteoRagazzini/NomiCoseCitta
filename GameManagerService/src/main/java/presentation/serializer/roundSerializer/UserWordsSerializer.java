package presentation.serializer.roundSerializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import model.round.UserWords;
import presentation.serializer.AbstractJsonSerializer;

public class UserWordsSerializer extends AbstractJsonSerializer<UserWords> {
    @Override
    protected JsonElement toJsonElement(UserWords object) {
        var jobj = new JsonObject();
        jobj.add("userID", new JsonPrimitive(object.getUserID()));
        object.getWords().forEach((cat, word) -> jobj.add(cat, new JsonPrimitive(word)));
        return jobj;
    }
}
