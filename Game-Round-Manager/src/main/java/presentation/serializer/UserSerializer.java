package presentation.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.User;

import java.util.logging.Handler;

public class UserSerializer extends AbstractJsonSerializer<User> {
    @Override
    protected JsonElement toJsonElement(User object) {
        JsonObject jobj = new JsonObject();
        jobj.addProperty("nickname", object.getNickname());
        jobj.addProperty("address", object.getAddress());
        return jobj;
    }
}
