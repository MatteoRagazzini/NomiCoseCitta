package presentation.deserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.User;
import model.request.StartRequest;

public class UserDeserializer extends AbstractJsonDeserializer<User>{
    @Override
    protected User deserializeJson(JsonElement jsonElement) {
        if(jsonElement.isJsonObject()){
            var jobj = (JsonObject) jsonElement;
            if(jobj.has("nickname") && jobj.get("nickname").isJsonPrimitive() &&
                    jobj.has("address") && jobj.get("address").isJsonPrimitive()){
                return new User(jobj.get("nickname").getAsString(), jobj.get("address").getAsString());
            }
        }

        throw new RuntimeException("cannot deserialize start game request");
    }
}
